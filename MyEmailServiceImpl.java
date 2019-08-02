package com.ingbank.banking.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.ingbank.banking.model.EmailModel;
import com.ingbank.banking.service.MyEmailService;

@Service
public class MyEmailServiceImpl implements MyEmailService {

		private JavaMailSender mailSender;

		@Autowired
		public MyEmailServiceImpl(JavaMailSender mailSender) 
		{
			this.mailSender = mailSender;
		}
		
		@Override
		public void sendMail(EmailModel emailModel) throws MailException
		{	
			SimpleMailMessage msg = new SimpleMailMessage();
			
			msg.setSubject(emailModel.getMailSubject());
			msg.setTo(emailModel.getMailTo());
			msg.setText(emailModel.getMailBody());
	        	
	        mailSender.send(msg);
		}
		
		


}
