package com.fandy.asynctaskdemo;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

/**
 * AsyncTask(抽象的泛型类)的源码学习
 * 不同的api版本AsyncTask具有不同的表现
 * AsyncTask并不适合执行特别耗时的人物操作.特别耗时的任务操作,建议使用线程池
 * 当前的AsyncTask也可以在子线程中创建,并且执行在子线程中
 */
public class MainActivity extends AppCompatActivity {

    private ProgressBar mPb;
    private ProgressBar mPb1;
    private ProgressBar mPb2;
    private int loadSuccessCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPb = (ProgressBar) findViewById(R.id.pb);
        mPb1 = (ProgressBar) findViewById(R.id.pb1);
        mPb2 = (ProgressBar) findViewById(R.id.pb2);


    /*    final DownLoadFileTask downLoadFileTask = new DownLoadFileTask(0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                downLoadFileTask.execute();
            }
        }).start();*/
        /**
         * 主线程中创建开启任务
         */
//        new DownLoadFileTask(0).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        new DownLoadFileTask(1).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        new DownLoadFileTask(2).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        /**
         * 子线程中开启任务
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                new DownLoadFileTask(0).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                new DownLoadFileTask(1).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                new DownLoadFileTask(2).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }).start();

    }

    /**
     * 模拟文件下载的任务类
     */
    private class DownLoadFileTask extends AsyncTask<Void, Integer, Integer> {

        private int tag;

        //构造方法
        public DownLoadFileTask(int i) {
            this.tag = i;
        }


        /**
         * 执行初始化的回调方法
         * <p>
         * 这个回调方法的执行线程和任务开启的线程是一样的和创建任务的线程是没有关系的
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("onPreExecute:" + Thread.currentThread().getName());
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            switch (tag) {
                case 0:
                    mPb.setProgress(values[0]);
                    break;
                case 1:
                    mPb1.setProgress(values[0]);
                    break;
                case 2:
                    mPb2.setProgress(values[0]);
                    break;
                default:
                    break;
            }

            System.out.println("onProgressUpdate:" + Thread.currentThread().getName());
        }


        /**
         * 在线程池中执行的后天任务回调放大
         *
         * @param params
         * @return
         */
        @Override
        protected Integer doInBackground(Void... params) {
            System.out.println("doInBackground:" + Thread.currentThread().getName());
            int totalSize = 0;
            for (int i = 1; i <= 100; i++) {
                try {
                    totalSize = i;
                   /* if (i == 60) {
                        cancel(true);
                        System.out.println("doInBackground结束了");
                    }*/
                    publishProgress(totalSize);
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return totalSize;
        }

        /**
         * 结束后的回调方法
         *
         * @param aLong
         */
        @Override
        protected void onPostExecute(Integer aLong) {
           /* System.out.println("onPostExecute:" + Thread.currentThread().getName());
            Toast.makeText(MainActivity.this, "下载结束", Toast.LENGTH_SHORT).show();*/
            loadSuccessCount++;
            if (loadSuccessCount == 3) {
                AlertDialog.Builder normalDialog =
                        new AlertDialog.Builder(MainActivity.this);
                normalDialog.setTitle("下载全部成功");
                normalDialog.setMessage("三个任务并行处理,监控所有的任务成功");
                normalDialog.show();
            }
        }
    }
}
