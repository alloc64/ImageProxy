package com.alloc64.imageproxy.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HashUtils {
    public static String sha256Base64(File file)
    {
        try
        {
            byte[] buffer = new byte[1024 * 1024];

            int count;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));

            while ((count = bis.read(buffer)) > 0)
                digest.update(buffer, 0, count);

            bis.close();

            return org.bouncycastle.util.encoders.Base64.toBase64String(digest.digest());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static String sha256Base62(String data)
    {
        try
        {
            return sha256Base62(data.getBytes(StandardCharsets.UTF_8));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return "";
    }

    public static String sha256Base62(byte[] data)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            digest.update(data);

            return new String(
                    io.seruco.encoding.base62.Base62
                            .createInstance()
                            .encode(digest.digest())
            );
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
