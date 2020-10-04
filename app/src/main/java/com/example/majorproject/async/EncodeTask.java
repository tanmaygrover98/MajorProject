package com.example.majorproject.async;

import android.graphics.Bitmap;
import android.net.Uri;

import com.example.majorproject.utils.BitmapUtils;
import com.example.majorproject.utils.FileUtils;
import com.example.majorproject.utils.SteganographyUtils;


public class EncodeTask extends SteganographyTask {
    public EncodeTask(AsyncResponse<SteganographyParams> delegate) {
        super(delegate);
    }

    /**
     * Encodes an image with the specified message, and saves it.
     * @param steganographyParams Contains filepath to image, and specified message
     * @return Contains filepath to encoded image.
     */
    @Override
    protected SteganographyParams execute(SteganographyParams steganographyParams) {

        Bitmap bitmap = BitmapUtils.decodeFile(steganographyParams.getFilePath());
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int numberOfPixels = w * h;

        byte[] data = steganographyParams.getMessage().getBytes();

        int requiredLength = data.length * 8 + 32;

        if (requiredLength > numberOfPixels) {
            throw new IllegalArgumentException("Message is too long to fit into pixels.");
        }

        int[] encodedPixels = SteganographyUtils.encode(
                BitmapUtils.getPixels(bitmap, requiredLength),
                steganographyParams.getMessage()
        );

        BitmapUtils.setPixels(bitmap, encodedPixels);

        Uri resultUri = FileUtils.saveBitmap(bitmap);

        steganographyParams.setResultUri(resultUri);

        steganographyParams.setType(AsyncResponse.Type.ENCODE_SUCCESS);
        return steganographyParams;
    }
}