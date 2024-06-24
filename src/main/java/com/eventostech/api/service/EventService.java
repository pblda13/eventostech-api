package com.eventostech.api.service;

import com.amazonaws.services.s3.AmazonS3;
import com.eventostech.api.domain.event.Event;
import com.eventostech.api.domain.event.EventsRequestDTO;
import com.eventostech.api.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Service
public class EventService {

    @Autowired
    private AmazonS3 S3Client;

    @Autowired
    private EventRepository repository;

    @Value("${aws.bucket.name}")
    private String bucketName;

    public Event createEvent(EventsRequestDTO data){
        String imgUrl = null;

        if(data.image() != null){
            imgUrl = this.uploadImg(data.image());
        }

        Event newEvent = new Event();
        newEvent.setTitle(data.title());
        newEvent.setDescription(data.description());
        newEvent.setEventUrl(data.eventUrl());
        newEvent.setDate(new Date(data.date()));
        newEvent.setImgUrl(imgUrl);
        newEvent.setRemote(data.remote());

        repository.save(newEvent);


        return newEvent;
    }

    private String uploadImg(MultipartFile multipartFile) {
        String filename = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();

        try {
            File file = this.convertMultipartToFile(multipartFile);
            S3Client.putObject(bucketName,filename ,file);
            file.delete();
            return  S3Client.getUrl(bucketName,filename).toString();
        }catch (Exception e){
            System.out.println("Erro ao subir arquivo");
            return "";
        }
    }

    private File convertMultipartToFile(MultipartFile multipartFile) throws IOException {

        File convFile = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(multipartFile.getBytes());
        fos.close();
        return convFile;
    }
}
