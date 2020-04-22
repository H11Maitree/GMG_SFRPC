package jp.jaxa.iss.kibo.rpc.defaultapk;

import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;

import android.graphics.Bitmap;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.ByteArrayOutputStream;

/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class YourService extends KiboRpcService {
    //Keep in zone  x       y       z
    //Min           10.25   -9.75   4.2
    //Max           11.65   -3      5.6
    //Mean          10.95   -6.375  4.9

    @Override
    protected void runPlan1(){
        // write here your plan 1
        api.judgeSendStart();
        moveToWrapper(10.95, -3.3, 4.9, 0, 0, 0, 1);
        String res=detectBarCode();
        if (res!=null){
            moveToWrapper(10.4, -3.3, 4.9, 0, 0, 0, 1);
        }

        moveToWrapper(10.95, -3.7, 4.9, 1, 0, 0, 0);
        res=detectBarCode();
        if (res!=null){
            moveToWrapper(10.4, -3.7, 4.9, 0, 0, 0, 1);
        }

        moveToWrapper(10.95, -4.4, 4.9, 0, 1, 0, 0);
        res=detectBarCode();
        if (res!=null){
            moveToWrapper(10.4, -4.4, 4.9, 0, 0, 0, 1);
        }

        moveToWrapper(10.95, -5.1, 4.9, 0, 0, 1, 0);
        res=detectBarCode();
        if (res!=null){
            moveToWrapper(10.4, -5.1, 4.9, 0, 0, 0, 1);
        }

        // Where should see some qr
        moveToWrapper(10.95, -5.8, 4.9, 0, 0, 0, 1);
        res=detectBarCode();
        if (res!=null){
            moveToWrapper(10.4, -5.8, 4.9, 0, 0, 0, 1);
        }

        moveToWrapper(10.95, -6.5, 4.9, 0, 0, 0, 1);
        res=detectBarCode();
        if (res!=null){
            moveToWrapper(10.95, -5.8, 4.9, 0, 0, 0, 1);
        }

        moveToWrapper(10.95, -7.2, 4.9, 0, 0, 0, 1);
        res=detectBarCode();
        if (res!=null){
            moveToWrapper(10.95, -7.2, 4.9, 0, 0, 0, 1);
        }

        moveToWrapper(10.95, -7.9, 4.9, 0, 0, 0, 1);
        res=detectBarCode();
        if (res!=null){
            moveToWrapper(10.95, -7.9, 4.9, 0, 0, 0, 1);
        }

        moveToWrapper(10.95, -8.6, 4.9, 0, 0, 0, 1);
        res=detectBarCode();
        if (res!=null){
            moveToWrapper(10.95, -8.6, 4.9, 0, 0, 0, 1);
        }

        moveToWrapper(10.95, -9.3, 4.9, 0, 0, 0, 1);
        res=detectBarCode();
        if (res!=null){
            moveToWrapper(10.95, -9.3, 4.9, 0, 0, 0, 1);
        }

        api.laserControl(true);
        api.judgeSendFinishSimulation();
    }

    @Override
    protected void runPlan2(){
        // write here your plan 2
    }

    @Override
    protected void runPlan3(){
        // write here your plan 3
    }

    //Barcode scanner code from Shakeeb Ayaz https://stackoverflow.com/a/39953598
    String detectBarCode() {
        Bitmap bitmap = api.getBitmapNavCam(); 
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        LuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
        
        Reader reader = new QRCodeReader();
        
        try {
            com.google.zxing.Result result = reader.decode(new BinaryBitmap(new HybridBinarizer(source)));
            return ((com.google.zxing.Result) result).getText();
        } catch (NotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (ChecksumException e) {
            e.printStackTrace();
            return null;
        } catch (FormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    //This func help to avoid tolerance violations error
    //More info in Kibo-RPC_PGManual 5.3 dealling randomness
    private void moveToWrapper(double pos_x, double pos_y, double pos_z,
                               double qua_x, double qua_y, double qua_z,
                               double qua_w){

        final int LOOP_MAX = 3;
        final Point point = new Point(pos_x, pos_y, pos_z);
        final Quaternion quaternion = new Quaternion((float)qua_x, (float)qua_y,
                                                     (float)qua_z, (float)qua_w);

        Result result = api.moveTo(point, quaternion, true);

        int loopCounter = 0;
        while(!result.hasSucceeded() || loopCounter < LOOP_MAX){
            result = api.moveTo(point, quaternion, true);
            ++loopCounter;
        }
                               }
}

