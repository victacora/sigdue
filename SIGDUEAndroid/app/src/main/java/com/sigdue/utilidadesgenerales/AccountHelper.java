package com.sigdue.utilidadesgenerales;

import java.security.MessageDigest;
import uk.co.senab.photoview.BuildConfig;

public class AccountHelper {
    public static final String md5(String toEncrypt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            digest.update(toEncrypt.getBytes());
            byte[] bytes = digest.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(String.format("%02X", new Object[]{Byte.valueOf(bytes[i])}));
            }
            return sb.toString().toLowerCase();
        } catch (Exception e) {
            return BuildConfig.FLAVOR;
        }
    }
}
