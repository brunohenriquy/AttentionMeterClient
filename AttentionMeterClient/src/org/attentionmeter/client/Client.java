package org.attentionmeter.client;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

import com.example.androidclient.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Client extends Activity {
	
	TextView textResponse;
	EditText editTextAddress, editTextPort; 
	Button buttonConnect, buttonClear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		editTextAddress = (EditText)findViewById(R.id.address);
		editTextPort = (EditText)findViewById(R.id.port);
		buttonConnect = (Button)findViewById(R.id.connect);
		buttonClear = (Button)findViewById(R.id.clear);
		textResponse = (TextView)findViewById(R.id.response);
		
		buttonConnect.setOnClickListener(buttonConnectOnClickListener);
		

	}
	
	OnClickListener buttonConnectOnClickListener = 
			new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					MyClientTask myClientTask = new MyClientTask(
							editTextAddress.getText().toString(),
							Integer.parseInt(editTextPort.getText().toString()));
					myClientTask.execute();
				}};

	public class MyClientTask extends AsyncTask<Void, Void, Void> {
		
		String dstAddress;
		int dstPort;
		String response = "";
		
		MyClientTask(String addr, int port){
			dstAddress = addr;
			dstPort = port;
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			
			Socket socket = null;
			
			try {
				socket = new Socket(dstAddress, dstPort);
				
				SocketClientSendThread socketClientSendThread = new SocketClientSendThread(
						socket);
				socketClientSendThread.run();
				

			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				response = "UnknownHostException: " + e.toString();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				response = "IOException: " + e.toString();
			}finally{
				if(socket != null){
					try {
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			textResponse.setText(response);
			super.onPostExecute(result);
		}
		
	}
	
	private class SocketClientSendThread extends Thread {

		private Socket hostThreadSocket;

		SocketClientSendThread(Socket socket) {
			hostThreadSocket = socket;
		}

		@Override
		public void run() {
			OutputStream outputStream;
			EditText ecg = (EditText) findViewById(R.id.Ecg);
			EditText gsr = (EditText) findViewById(R.id.Gsr);
			EditText eeg = (EditText) findViewById(R.id.Eeg);
			EditText emg = (EditText) findViewById(R.id.Emg);
			String str = ecg.getText().toString() + "-" + gsr.getText().toString() + "-" + eeg.getText().toString() + "-" + emg.getText().toString();

			try {
				outputStream = hostThreadSocket.getOutputStream();
	            PrintStream printStream = new PrintStream(outputStream);
	            printStream.print(str);
	            printStream.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
