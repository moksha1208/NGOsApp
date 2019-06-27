package com.d.ngosapp;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

public class BackgroundWorker1 extends AsyncTask<String, Void, String> {
    Context context;

    BackgroundWorker1(Context ctx) {
        context = ctx;
    }

    @Override
    protected String doInBackground(String... strings) {
        String type = strings[0];
        String getphonenumber_url = "http://10.0.0.146/getphonenumber.php";
        String sendmessage_url = "http://10.0.0.146/sendmessage.php";
        String intracking_url = "http://10.0.0.146/intracking.php";
        String addngo_url = "http://10.0.0.146/addngo.php";
        String getdonoraddress_url = "http://10.0.0.146/getdonoraddress.php";
        if (type.equals("getphonenumber")) {
            try {
                System.out.println("IN bg1");
                URL url = new URL(getphonenumber_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setInstanceFollowRedirects(true);
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String line = bufferedReader.readLine();
                System.out.println("Buffered" + line);
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                System.out.println("Line " + line);
                return line;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (type.equals("sendmessage")) {
            try {
                URL url = new URL(sendmessage_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setInstanceFollowRedirects(true);
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String line = bufferedReader.readLine();
                System.out.println("Buffered"+line);
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                System.out.println("Line "+line);
                return line;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (type.equals("intracking")) {
            try {
                String donor_phone_no = strings[1];
                String ngo_name = strings[2];
                String ngo_lat = strings[3];
                String ngo_lng = strings[4];
                URL url = new URL(intracking_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setInstanceFollowRedirects(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("donor_phone_no", "UTF-8") + "=" + URLEncoder.encode(donor_phone_no, "UTF-8") + "&" +
                        URLEncoder.encode("ngo_name", "UTF-8") + "=" + URLEncoder.encode(ngo_name, "UTF-8") + "&" +
                        URLEncoder.encode("ngo_lat", "UTF-8") + "=" + URLEncoder.encode(ngo_lat, "UTF-8") + "&" +
                        URLEncoder.encode("ngo_lng", "UTF-8") + "=" + URLEncoder.encode(ngo_lng, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String line = bufferedReader.readLine();
                System.out.println("Buffered"+line);
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                System.out.println("Line "+line);
                return line;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (type.equals("addngo")) {
            try {
                String ngo_name = strings[1];
                String ngo_address = strings[2];
                String ngo_number = strings[3];
                String ngo_password = strings[4];
                URL url = new URL(addngo_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setInstanceFollowRedirects(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("ngo_name", "UTF-8") + "=" + URLEncoder.encode(ngo_name, "UTF-8") + "&" +
                        URLEncoder.encode("ngo_address", "UTF-8") + "=" + URLEncoder.encode(ngo_address, "UTF-8") + "&" +
                        URLEncoder.encode("ngo_number", "UTF-8") + "=" + URLEncoder.encode(ngo_number, "UTF-8") + "&" +
                        URLEncoder.encode("ngo_password", "UTF-8") + "=" + URLEncoder.encode(ngo_password, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return "Ngo Added";

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (type.equals("getdonoraddress")) {
            try {
                String donor_phone_no = strings[1];
                URL url = new URL(getdonoraddress_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setInstanceFollowRedirects(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("phoneno", "UTF-8") + "=" + URLEncoder.encode(donor_phone_no, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String line = bufferedReader.readLine();
                System.out.println("Buffered"+line);
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                System.out.println("Line "+line);
                return line;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
