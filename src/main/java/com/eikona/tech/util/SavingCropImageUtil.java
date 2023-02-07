package com.eikona.tech.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Base64;

import org.springframework.stereotype.Component;

import com.eikona.tech.constants.ApplicationConstants;
import com.eikona.tech.constants.DefaultConstants;
import com.eikona.tech.constants.NumberConstants;
import com.eikona.tech.entity.FileSystemContent;
import com.eikona.tech.entity.Transaction;
import com.eikona.tech.repository.FileRepository;
import com.eikona.tech.service.FileContentStore;

@Component
public class SavingCropImageUtil {
	
//	@Autowired
	private FileContentStore contentStore;
	
//	@Autowired
	private FileRepository filerepo;
	
	
	public SavingCropImageUtil(FileContentStore contentStore, FileRepository filerepo) {
		this.contentStore = contentStore;
		this.filerepo = filerepo;
	}



	public String saveCropImages(String base64, Transaction trans) {
		SimpleDateFormat contentDateFolderformat = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_INDIA_WITHOUT_DELIMITER);
		SimpleDateFormat contentImageFileformat = new SimpleDateFormat(ApplicationConstants.DATE_TIME_FORMAT_OF_INDIA_WITHOUT_DELIMITER);

		byte[] decodedBytes = Base64.getDecoder().decode(base64);
		InputStream inputStream = new ByteArrayInputStream(decodedBytes);

		FileSystemContent fileObj = new FileSystemContent();
		fileObj.setContentMimeType(ApplicationConstants.MIME_TYPE_JPG);
		String dateFolder = contentDateFolderformat.format(trans.getPunchDate());
		String imageFileDate = contentImageFileformat.format(trans.getPunchDate());
		fileObj.setContentPath(fileObj.getContentMimeType().split(ApplicationConstants.DELIMITER_FORWARD_SLASH)[NumberConstants.ZERO] 
				+ ApplicationConstants.DELIMITER_FORWARD_SLASH +DefaultConstants.TRANSACTION_STORE_FOLDER+ApplicationConstants.DELIMITER_FORWARD_SLASH + dateFolder 
				+ ApplicationConstants.DELIMITER_FORWARD_SLASH + imageFileDate + ApplicationConstants.DELIMITER_HYPHEN + trans.getEmpId() + ApplicationConstants.EXTENSION_JPG);

		String path = DefaultConstants.CONTENT_STORE_ROOT_PATH + fileObj.getContentPath();
		contentStore.setContent(fileObj, inputStream);
		filerepo.save(fileObj);

		return path;

	}
}
