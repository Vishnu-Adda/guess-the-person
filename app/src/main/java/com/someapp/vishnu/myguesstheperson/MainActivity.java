package com.someapp.vishnu.myguesstheperson;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> peopleURLs = new ArrayList<String>();
    ArrayList<String> peopleNames = new ArrayList<String>();
    String[] answers = new String[4];
    int locationOfCorrectAnswer = 0;

    int selectedPerson = 0;

    ImageView imageView;
    Button button0;
    Button button1;
    Button button2;
    Button button3;

    public void personChosen(View view) {

        if(view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))) {

            // Third param determines how long the text will show
            Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();

        } else {

            Toast.makeText(getApplicationContext(),
                    "Wrong! It was " + peopleNames.get(selectedPerson),
                        Toast.LENGTH_SHORT).show();;

        }

        update();

    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(String... urls) {

            try{

                URL url = new URL(urls[0]);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                Bitmap myBitmap = new BitmapFactory().decodeStream(inputStream);

                return myBitmap;

            } catch (Exception e) {

                e.printStackTrace();

                return null;

            }

        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;

                    result += current;
                    data = reader.read();

                }

                return result;

            } catch (Exception e) {

                e.printStackTrace();
                return null;

            }

        }
    }

    public void update() {

        try {

            Random random = new Random();

            selectedPerson = random.nextInt(peopleURLs.size());

            ImageDownloader imageDownloader = new ImageDownloader();

            Bitmap personImage = imageDownloader.execute(peopleURLs.get(selectedPerson)).get();
            imageView.setImageBitmap(personImage);

            locationOfCorrectAnswer = random.nextInt(4);

            int incorrectAnswerLocation;

            for (int i = 0; i < 4; i++) {

                if (i == locationOfCorrectAnswer) {

                    answers[i] = peopleNames.get(selectedPerson);

                } else {

                    incorrectAnswerLocation = random.nextInt(peopleURLs.size());

                    while (incorrectAnswerLocation == selectedPerson) {

                        incorrectAnswerLocation = random.nextInt(peopleURLs.size());

                    }

                    answers[i] = peopleNames.get(incorrectAnswerLocation);

                }

            }

            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);
        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        button0 = findViewById(R.id.buttonAnswer0);
        button1 = findViewById(R.id.buttonAnswer1);
        button2 = findViewById(R.id.buttonAnswer2);
        button3 = findViewById(R.id.buttonAnswer3);

        DownloadTask downloadTask = new DownloadTask();
        String result = "";

        try {

            result = downloadTask.execute("http://www.posh24.se/kandisar").get();

//            Log.i("HTML content", result);

            // slashes '\' are automatically added by Android Studio
            // splitContent is an array of 2 strings, as the below expression occurs only once
            String[] splitContent = result.split("<div class=\"listedArticles\"");

            Pattern pattern = Pattern.compile("img src=\"(.*?)\"");
            Matcher matcher = pattern.matcher(splitContent[0]);

            while (matcher.find()) {

                System.out.println(matcher.group(1));
                peopleURLs.add(matcher.group(1));

            }

            pattern = Pattern.compile("alt=\"(.*?)\"");
            matcher = pattern.matcher(splitContent[0]);

            while (matcher.find()) {

                System.out.println(matcher.group(1));
                peopleNames.add(matcher.group(1));

            }

            update();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }
}
