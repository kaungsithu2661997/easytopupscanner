/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.mit.easytopupscanner;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import com.example.mit.easytopupscanner.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;

import java.text.NumberFormat;
import java.text.ParsePosition;

/**
 * A very simple Processor which gets detected TextBlocks and adds them to the overlay
 * as OcrGraphics.
 */
public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {
    int count=0;Context context;
    private  MyCardInterface myCardInterface;
    OcrDetectorProcessor()
    {

    }
  public static String value="";
    private GraphicOverlay<OcrGraphic> graphicOverlay;

    OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay,MyCardInterface myCardInterface) {
        graphicOverlay = ocrGraphicOverlay;
        this.myCardInterface=myCardInterface;
    }

    public static boolean isNumeric(String str)
    {
        NumberFormat format=NumberFormat.getInstance();
        ParsePosition pos=new ParsePosition(0);
       format.parse(str,pos);
        return  str.length()==pos.getIndex();
    }

    public  String  getValue()
    {
        return value;
    }
    public  static  String isCheck_pinnumber(String str)
    {
        Log.e("string",str);
        if(str.contains(" ")) {
            String lineWithoutSpaces = str.replaceAll("\\s+", "");
            if (lineWithoutSpaces.length() >= 13) {
                return lineWithoutSpaces;
            } else {
                return null;
            }
        }
        else {return null;}
    }
    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {

        graphicOverlay.clear();
        String imageText="";
        SparseArray<TextBlock> items = detections.getDetectedItems();
        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);
            imageText=item.getValue();
           if(isNumeric(imageText)){
               String ss=isCheck_pinnumber(imageText);

                if(ss!=null)
                {
                   if(count==0) {
                        myCardInterface.sendData(ss);
                        count=1;
                       break;
                    }
                }
            }
            if (item != null && item.getValue() != null) {
                Log.d("OcrDetectorProcessor", "Text detected! " + item.getValue());
                OcrGraphic graphic = new OcrGraphic(graphicOverlay, item);
                graphicOverlay.add(graphic);
            }
        }

    }

    /**
     * Frees the resources associated with this detection processor.
     */
    @Override
    public void release() {
        graphicOverlay.clear();
    }

}
