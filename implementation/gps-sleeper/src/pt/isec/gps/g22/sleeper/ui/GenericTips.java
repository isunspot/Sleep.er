package pt.isec.gps.g22.sleeper.ui;

import java.util.ArrayList;
import java.util.Random;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GenericTips extends Activity {

	TextView tvTips;
	LinearLayout nextLayout;
	ArrayList<String> tips;
	int previousRandomNumber = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_generic_tips);
		
		hideActionBar();
		tvTips = (TextView) findViewById(R.id.tvTips);
		nextLayout = (LinearLayout) findViewById(R.id.LayoutNext);
		
		setUpTips();
		tvTips.setText(getRandomTip());
		
		nextLayout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				tvTips.setText(getRandomTip());
			}
		});
		
	}
	
	private int getRandomNumber() {
		Random rand = new Random();
		int randomNumber = rand.nextInt(tips.size());
		if(randomNumber == previousRandomNumber)
			return getRandomNumber();
		previousRandomNumber = randomNumber;
		return randomNumber;
	}
	
    private void hideActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.hide();
    }
	
	private String getRandomTip() {
		return tips.get(getRandomNumber());
	}
	
	private void setUpTips() {
		tips = new ArrayList<String>();
		tips.add("Sleep deprivation affects your attention, alertness, concentration, reasoning, and capacity of problem solving");
		tips.add("Sleep disorders and chronic sleep loss are related with health problems: Heart disease/attack, High blood pressure, stroke, diabetes");
		tips.add("Lack of sleep and sleep disorders can contribute to the symptoms of depression");
		tips.add("Lack of sleep seems to be related to an increase in hunger and appetite, and possibly to obesity");
		tips.add("Lack of sleep doubled the risk of death from cardiovascular disease");
		tips.add("Lack of sleep can affect our interpretation of events");
		tips.add("Lack of sleep affects your ability to think and process information");
		tips.add("All sorts of different studies are pointing to how sleep deprivation damages brain cells");
		tips.add("Driving while sleep deprived can actually be worse than driving drunk — it has many of the same effects, but is way less obvious to the driver");
		tips.add("Lack of sleep Increases food consumption and appetite");
	}
}
