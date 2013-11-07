package com.example.crazytipcalc;

import android.os.Bundle;
import android.os.SystemClock;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

public class CrazyTipCalc extends Activity {

	private static final String TOTAL_BILL = "TOTAL_BILL";
	private static final String CURRENT_TIP = "CURRENT_BILL";
	private static final String BILL_WITHOUT_TIP = "BILL_WITHOUT_TIP";

	private double billBeforeTip;
	private double tipAmount;
	private double finalBill;

	EditText billBeforeTipET;
	EditText tipAmountET;
	EditText finalBillET;

	SeekBar tipSeekBar;

	private int[] checklistValues = new int[12];

	CheckBox friendlyCheckBox;
	CheckBox specialsCheckBox;
	CheckBox opinionCheckBox;

	RadioGroup availableRadioGroup;
	RadioButton availableBadRadio;
	RadioButton availableOkRadio;
	RadioButton availableGoodRadio;

	Spinner problemsSpinner;

	Button startChronometerButton;
	Button pauseChronometerButton;
	Button resetChronometerButton;

	Chronometer timeWaitingChronometer;

	long secondsYouWaited = 0;

	TextView timeWatingTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crazy_tip_calc);

		if (savedInstanceState == null){
			billBeforeTip = 0.0;
			tipAmount = .15;
			finalBill = 0.0;
		} else {
			billBeforeTip = savedInstanceState.getDouble(BILL_WITHOUT_TIP);
			tipAmount = savedInstanceState.getDouble(CURRENT_TIP);
			finalBill = savedInstanceState.getDouble(TOTAL_BILL);
		}

		billBeforeTipET = (EditText) findViewById(R.id.billEditText);
		tipAmountET = (EditText) findViewById(R.id.tipEditText);
		finalBillET = (EditText) findViewById(R.id.finalBillEditText);

		tipSeekBar = (SeekBar) findViewById(R.id.tipSeekBar);
		tipSeekBar.setOnSeekBarChangeListener(tipSeekBarChangeListener);

		billBeforeTipET.addTextChangedListener(billBeforeTipListener);

		friendlyCheckBox = (CheckBox) findViewById(R.id.friendlyCheckBox);
		specialsCheckBox = (CheckBox) findViewById(R.id.specialsCheckBox);
		opinionCheckBox = (CheckBox) findViewById(R.id.opinionCheckBox);

		setUpIntroCheckBoxes();

		availableRadioGroup = (RadioGroup) findViewById(R.id.availableRadioGroup);
		availableBadRadio = (RadioButton) findViewById(R.id.availableBadRadio);
		availableOkRadio = (RadioButton) findViewById(R.id.availableOkRadio);
		availableGoodRadio = (RadioButton) findViewById(R.id.availableGoodRadio);

		addChangeListenersToRadios();

		problemsSpinner = (Spinner) findViewById(R.id.problemSpinner);

		addItemSelectedListenerToSpinner();

		startChronometerButton = (Button) findViewById(R.id.startChronometerButton);
		pauseChronometerButton = (Button) findViewById(R.id.pauseChronometerButton);
		resetChronometerButton = (Button) findViewById(R.id.resetChronometerButton);

		setButtonOnClickListeners();

		timeWaitingChronometer = (Chronometer) findViewById(R.id.timeWaitingChronometer);

		timeWatingTextView = (TextView) findViewById(R.id.timeWaitingTextView);

	}

	private TextWatcher billBeforeTipListener = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			try {
				billBeforeTip = Double.parseDouble(s.toString());
			}
			catch(NumberFormatException e){
				billBeforeTip = 0.0;
			}
			updateTipAndFinalBill();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}
	};

	private OnSeekBarChangeListener tipSeekBarChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			tipAmount = (tipSeekBar.getProgress()) * 0.01;
			tipAmountET.setText(String.format("%.02f", tipAmount));
			updateTipAndFinalBill();

		}
	};

	@Override
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);

		outState.putDouble(TOTAL_BILL, finalBill);
		outState.putDouble(CURRENT_TIP, tipAmount);
		outState.putDouble(BILL_WITHOUT_TIP, billBeforeTip);

	}

	private void updateTipAndFinalBill(){
		tipAmount = Double.parseDouble(tipAmountET.getText().toString());
		finalBill = billBeforeTip + billBeforeTip * tipAmount;
		finalBillET.setText(String.format("%.02f", finalBill));
	}

	private void setUpIntroCheckBoxes(){
		friendlyCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				checklistValues[0] = (friendlyCheckBox.isChecked())?4:0;
				setTipFromWaitressChecklist();

				updateTipAndFinalBill();

			}

		});

		specialsCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				checklistValues[1] = (specialsCheckBox.isChecked())?1:0;

				setTipFromWaitressChecklist();

				updateTipAndFinalBill();
			}
		});

		opinionCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				checklistValues[2] = (opinionCheckBox.isChecked())?2:0;
				setTipFromWaitressChecklist();

				updateTipAndFinalBill();
			}
		});
	}

	private void setTipFromWaitressChecklist(){
		int checkListTotal = 0;

		for (int item : checklistValues){
			checkListTotal += item;
		}

		tipAmountET.setText(String.format("%.02f", checkListTotal * .01));

	}

	private void addChangeListenersToRadios(){

		availableRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				checklistValues[3] = (availableBadRadio.isChecked())?-1:0;
				checklistValues[4] = (availableOkRadio.isChecked())?2:0;
				checklistValues[5] = (availableGoodRadio.isChecked())?4:0;

				setTipFromWaitressChecklist();
				updateTipAndFinalBill();

			}
		});

	}
	
	private void addItemSelectedListenerToSpinner(){
		problemsSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				
				checklistValues[6] = (problemsSpinner.getSelectedItem().equals("Bad"))?-1:0;
				checklistValues[7] = (problemsSpinner.getSelectedItem().equals("Ok"))?3:0;
				checklistValues[8] = (problemsSpinner.getSelectedItem().equals("Good"))?6:0;
				
				setTipFromWaitressChecklist();
				updateTipAndFinalBill();
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private void setButtonOnClickListeners(){
		startChronometerButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int stoppedMilliseconds = 0;
				
				String chronoText = timeWaitingChronometer.getText().toString();
				String array[] = chronoText.split(":");
				
				if (array.length == 2){
					
					stoppedMilliseconds = Integer.parseInt(array[0]) * 60 * 1000 +
							Integer.parseInt(array[1]) * 1000;
					
				} else if (array.length == 3){
					
					stoppedMilliseconds = Integer.parseInt(array[0]) * 60 * 60 * 1000 +
							Integer.parseInt(array[1]) * 60 * 1000 +
							Integer.parseInt(array[2]) * 1000;
					
				}
				
				timeWaitingChronometer.setBase(SystemClock.elapsedRealtime() - stoppedMilliseconds);
				
				secondsYouWaited = Long.parseLong(array[1]);
				
				updateTipBasedOnTImeWaited(secondsYouWaited);
				
				timeWaitingChronometer.start();
				
			}
		});
		
		
	pauseChronometerButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				timeWaitingChronometer.stop();
				
			}
		});
	
	resetChronometerButton.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			timeWaitingChronometer.setBase(SystemClock.elapsedRealtime());
			secondsYouWaited = 0;
			
		}
	});
	
	}
	
	private void updateTipBasedOnTImeWaited(long secondsYouWaited){
		checklistValues[9] = (secondsYouWaited > 10) ?  -2 : 0;

		setTipFromWaitressChecklist();
		updateTipAndFinalBill();
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.crazy_tip_calc, menu);
		return true;
	}

}
