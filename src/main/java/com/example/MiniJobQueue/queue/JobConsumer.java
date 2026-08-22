package com.example.MiniJobQueue.queue;

import com.example.MiniJobQueue.entity.Job;
import com.example.MiniJobQueue.enums.JobStatus;
import com.example.MiniJobQueue.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class JobConsumer {

    private final JobRepository jobRepository;
    private static final Integer MAX_RETRIES=3;
    private final JobProducer jobProducer;

    @RabbitListener(queues = "job.queue")
    public void consumeJob(Long id)  {
        Job job=jobRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Job not found"));
        try {
            job.setStatus(JobStatus.PROCESSING);
            job.setUpdatedAt(LocalDateTime.now());
            jobRepository.save(job);
            //worker does work
            String filename = "report_" + job.getId() + ".txt";
            File directory = new File("reports");
            if (!directory.exists()) directory.mkdirs();

            File file = new File(directory, filename);
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("JOB REPORT\n");
                writer.write("--------------");
                writer.write("Job ID: " + job.getId() + "\n");
                writer.write("Job Type: " + job.getType() + "\n");
                writer.write("Generated at: " + LocalDateTime.now() + "\n");
                writer.write("Status: COMPLETED\n");
            }
            job.setResult(file.getAbsolutePath());
            job.setStatus(JobStatus.COMPLETED);
            job.setUpdatedAt(LocalDateTime.now());
            jobRepository.save(job);
        }catch (IOException e){
            int retries=job.getRetryCount();
            retries++;
            if(retries<=MAX_RETRIES) {
                job.setRetryCount(retries);
                job.setStatus(JobStatus.QUEUED);
                job.setUpdatedAt(LocalDateTime.now());
                jobRepository.save(job);
                jobProducer.sendRetryJob(id,retries);
            }
            else {
                job.setStatus(JobStatus.FAILED);
                job.setUpdatedAt(LocalDateTime.now());
                jobRepository.save(job);
            }
        }
    }
}
