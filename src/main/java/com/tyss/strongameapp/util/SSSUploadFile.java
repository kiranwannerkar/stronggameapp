package com.tyss.strongameapp.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.tyss.strongameapp.exception.FailedToUploadException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SSSUploadFile {
	/**
	 * This is the end point url for amazon s3 bucket
	 */
	@Value("${amazonProperties.endpointUrl}")
	private String endpointUrl;
	/**
	 * This is the bucketName for amazon s3 bucket
	 */
	@Value("${amazonProperties.bucketName}")
	public String bucketName;

	/**
	 * This enables automatic dependency injection of AmazonS3 interface. This
	 * object is used by methods in the SimpleProductServiceImplementation to call
	 * the respective methods.
	 */
	@Autowired
	private AmazonS3 s3client;

	public String uploadFile(MultipartFile multipartFile, int id, String name) {
		String fileUrl = "";
		try {
			File file = convertMultiPartFiletoFile(multipartFile);
			String fileName = generateFileName(multipartFile);
			fileUrl = uploadFileTos3bucketConfig(file, id, fileName, name);
		} catch (Exception e) {
			throw new FailedToUploadException("Failed to upload.");
		}
		return fileUrl;
	}

	private File convertMultiPartFiletoFile(MultipartFile file) {
		File convertedFile = new File(file.getOriginalFilename());
		try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
			fos.write(file.getBytes());
		} catch (IOException e) {
			log.error("Error while converting multipart file to file.", e);
		}
		return convertedFile;
	}

	private String generateFileName(MultipartFile multiPart) {

		String fileName = multiPart.getOriginalFilename();
		if (fileName != null) {
			return new Date().getTime() + "-" + fileName.replace(" ", "_");
		} else {
			throw new FailedToUploadException("File name is empty.");
		}

	}

	public String uploadFileTos3bucketConfig(File file, int id, String fileName, String name) {
		String filePath = name + File.separator + id + "/file/" + fileName;
		s3client.putObject(
				new PutObjectRequest(bucketName, filePath, file).withCannedAcl(CannedAccessControlList.PublicRead));
		return s3client.getUrl(bucketName, filePath).toString();
	}

	public void deleteS3Folder(String bucketName, String folderPath) {
		for (S3ObjectSummary file : s3client.listObjects(bucketName, folderPath).getObjectSummaries()) {
			s3client.deleteObject(bucketName, file.getKey());
		}
	}
}
