package com.ingbank.banking.service.impl;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.ingbank.banking.exception.ApplicationException;
import com.ingbank.banking.service.OtpService;

@Service
public class OtpServiceImpl implements OtpService {
	

	
	 private static final Integer EXPIRE_MINS = 5;
	 private LoadingCache<String, Integer> otpCache;

	private Object random;
	 
	 public OtpServiceImpl()
	 {
		 super();
		 otpCache = CacheBuilder.newBuilder().
		     expireAfterWrite(
		    		 EXPIRE_MINS, TimeUnit.MINUTES).build(new CacheLoader<String, Integer>() {
		    			 	public Integer load(String referenceId) {
		    			 				return 0;
		    			 	}
		    		 });
		 }
	 
	//This method is used to push the opt number against Key. Rewrite the OTP if it exists
	 //Using user id  as key
	public int generateOTP(String referenceId) throws NoSuchAlgorithmException {
		Random rand = SecureRandom.getInstanceStrong();
		int otp = 100000 + rand.nextInt(900000);
		otpCache.put(referenceId, otp);
		return otp;
	}
	 
	 //This method is used to return the OPT number against Key->Key values is username
	 public int getOtp(String referenceId)
	 { 
		 try{
			 return otpCache.get(referenceId); 
		 }catch (Exception e){
			 return 0; 
		 }
	 }
	 
	//This method is used to clear the OTP catched already
	public void clearOTP(String referenceId)
	{ 
		otpCache.invalidate(referenceId);
	}
	
	public int processOtp(String referenceId) throws ApplicationException, NoSuchAlgorithmException
	{
		return generateOTP(referenceId);
	}
	
	
	 public boolean processValidOtp(int otpnum,String referenceId) 
	 {
		int serverOtp = getOtp(referenceId);
		if (serverOtp == otpnum)
			return true;
		else {
			clearOTP(referenceId);
			return false;
		}
			
	 }


}
