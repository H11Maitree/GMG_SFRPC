package jp.jaxa.iss.kibo.rpc.defaultapk;

import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.ReaderException;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import org.opencv.android.Utils;
import org.opencv.aruco.Aruco;
import org.opencv.aruco.Dictionary;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class YourService extends KiboRpcService {
    //Keep in zone  x       y       z
    //Min           10.25   -9.75   4.2
    //Max           11.65   -3      5.6
    //Mean          10.95   -6.375  4.9
    double pos_x=0;
    double pos_y=0;
    double pos_z=0;
    double qua_x=0;
    double qua_y=0;
    double qua_z=0;
    double qua_w=0;

    @Override
    protected void runPlan1(){
        // write here your plan 1
        api.judgeSendStart();
        Log.i("Plan1","judgeSendStart Done");
        moveToWrapper(10.6, -4.3, 5, 0, 0, -0.7071068, 0.7071068);

        String scaned = detectQR();
        api.judgeSendDiscoveredQR(0, scaned );
        pos_x = Double.valueOf(scaned.substring(7));
        Log.i("Plan1","pos no 0");

        moveToWrapper(11, -4.3, 5, 0, 0, -0.7071068, 0.7071068);
        scaned = detectQR();
        api.judgeSendDiscoveredQR(0, scaned );
        pos_x = Double.valueOf(scaned.substring(7));
        Log.i("Plan1","pos no 1");

        moveToWrapper(11, -5.7, 5, 0, 0, -0.7071068, 0.7071068);
        scaned = detectQR();
        api.judgeSendDiscoveredQR(0, scaned );
        pos_x = Double.valueOf(scaned.substring(7));
        Log.i("Plan1","pos no 2");

        moveToWrapper(11.5, -5.7, 4.5, 0, 0, 0, 1);
        Log.i("Plan1","pos no 3");

        moveToWrapper(11, -6, 5.55, 0, -0.7071068, 0, 0.7071068);
        Log.i("Plan1","pos no 4");


        moveToWrapper(11.1, -6, 5.55, 0, -0.7071068, 0, 0.7071068);
        Log.i("Plan1","pos no 5");

        Log.i("Plan1","pos no 6");
    }

    @Override
    protected void runPlan2(){
        // write here your plan 2
    }

    @Override
    protected void runPlan3(){
        // write here your plan 3
    }

    void movetotarget(double ARx, double ARy, double Pxw){
        if(pos_y>-9.55){
            pos_y=-9.55;
        }
        Log.i("Movetotarget","init");
        moveToWrapper((pos_x+0.1414+((ARx-640)*(0.05/Pxw))), pos_y, (pos_z-0.1414-((ARy-480)*(0.05/Pxw))), 0, 0, 0.707, -0.707);
        Log.i("Movetotarget","Moved");
        api.laserControl(true);
        Log.i("Movetotarget","lasered");
        api.judgeSendFinishSimulation();
    }

    void detectAR(){
        Bitmap bMap=api.getBitmapNavCam();
        Log.i("AR_modul","Get Bitmap");
        Mat mat= new Mat();
        Dictionary dictionary = Aruco.getPredefinedDictionary(Aruco.DICT_5X5_250);
        Utils.bitmapToMat(bMap,mat);
        List<Mat> cornors= new ArrayList<Mat>();
        Mat ids= new Mat();
        Log.i("AR_modul","Set up done");
        Aruco.detectMarkers(mat,dictionary,cornors,ids);
        Log.i("AR_modul","ids : " + ids.dump());
        Log.i("AR_modul","cornors : " + cornors.get(0).dump());
        double id=ids.get(0,0)[0];
        Mat cornor=cornors.get(0);
        double centre_X = (cornor.get(0,0)[0]+cornor.get(2,0)[0])/2;
        double centre_Y = (cornor.get(0,1)[0]+cornor.get(2,1)[0])/2;
        double pixel_w = (cornor.get(0,0)[0]-cornor.get(2,0)[0]);
        Log.i("AR_modul","Get all param");
        if(pixel_w<0){
            pixel_w= (-1)*pixel_w;
        }
        Log.i("AR_modul","Calling movetotarget");
        movetotarget(centre_X,centre_Y,pixel_w);

    }
    //Barcode scanner code from Shakeeb Ayaz https://stackoverflow.com/a/39953598
    String detectQR() {
        Log.i("QR_modul","Check0 init func detectBarcode");

        Bitmap bMap=api.getBitmapNavCam();
        Log.i("QR_modul","Get bitmap");

        int[] intArray = new int[bMap.getWidth()*bMap.getHeight()];
        //copy pixel data from the Bitmap into the 'intArray' array
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
        Log.i("QR_modul","LuminanceSource");

        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Log.i("QR_modul","Get binarybitmap");

        QRCodeReader reader= new QRCodeReader();
        Log.i("QR_modul","Setup reader");

        com.google.zxing.Result result=null;
        try{
            result=reader.decode(bitmap);
            Log.i("QR_modul","Get result");
        } catch (ReaderException e){
            Log.i("QR_modul","Reader ERROR");
            return "Reader ERROR";
        }
        Log.i("QR_modul","Result : " + result.getText());
        return result.getText();

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

