package com.learning.authentication.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class BarCodeUtilities {

    public static void createQRCode(String barCodeData, String filePath, int height, int width) throws WriterException, IOException {
        BitMatrix matrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE,width,height);
        try(FileOutputStream out = new FileOutputStream(filePath)){
            MatrixToImageWriter.writeToStream(matrix,"png",out);
        }
    }

    public static String getBarCodeUrl(String emailId, String mfaSecretKey){
        String companyName = "learning";
        return getGoogleAuthenticationBarcode(mfaSecretKey,emailId,companyName);
    }

    public static String getGoogleAuthenticationBarcode(String mfaSecretKey, String account, String issuer){
        try{
            return "otpauth://totp/"
                    + URLEncoder.encode(issuer+":"+account,"UTF-8").replace("+","%20")
                    +"?secret="+URLEncoder.encode(mfaSecretKey,"UTF-8").replace("+","%20")
                    +"&issuer="+URLEncoder.encode(issuer,"UTF-8").replace("+","%20");
        }catch (UnsupportedEncodingException e){
            throw new IllegalStateException(e);
        }
    }
}
