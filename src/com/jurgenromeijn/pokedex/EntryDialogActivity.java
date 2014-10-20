package com.jurgenromeijn.pokedex;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Display the found pokemon.
 */
public class EntryDialogActivity extends Activity {
	private ImageView _imageView;
	private TextView _textView;
	private ActionBar _actionBar;

    /**
     * Setup all references to UI elements.
     *
     * @param savedInstanceState
     */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in, R.anim.stay);
        setContentView(R.layout.entry);
        
        _actionBar = getActionBar();
        _actionBar.setDisplayHomeAsUpEnabled(true);
        _imageView = (ImageView)findViewById(R.id.pokemonImage);
        _textView = (TextView)findViewById(R.id.pokemonInfo);

        Bundle bundle = getIntent().getExtras();
        setPokemon(bundle.getString("pokemon"));
	}

    /**
     * Go back to the main actity when the icon in the actiobar is clicked (Android convention).
     *
     * @param item
     * @return
     */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            finish();
	    }
	    return super.onOptionsItemSelected(item);
	}

    /**
     * Show a pokemon. (I know, should be done differently, with a database or something, but
     * lacking time).
     *
     * @param pokemon
     */
	public void setPokemon(String pokemon) {
		showNotFound();
		if(pokemon.equals("pikachu")) {
			showPikachu();
		} else if(pokemon.equals("bulbasaur")) {
			showBulbasaur();
		} else if(pokemon.equals("charmander")) {
			showCharmander();
		} else if(pokemon.equals("squirtle")) {
			showSquirtle();
		} else {
			showNotFound();
		}
	}

    /**
     * Show Bulbasaur.
     */
	private void showBulbasaur()
	{
		_actionBar.setTitle("Bulbasaur");
		_actionBar.setLogo(R.drawable.bulbasaur_icon);
		_imageView.setImageResource(R.drawable.bulbasaur);
		_textView.setText("A strange seed was planted on its back at birth. The plant sprouts and grows with this Pok�mon.");
	}

    /**
     * Show Charmander.
     */
	private void showCharmander()
	{
		_actionBar.setTitle("Charmander");
		_actionBar.setLogo(R.drawable.charmander_icon);
		_imageView.setImageResource(R.drawable.charmander);
		_textView.setText("Obviously prefers hot places. When it rains, steam is said to spout from the tip of its tail.");
	}

    /**
     * Show Squirtle.
     */
	private void showSquirtle()
	{
		_actionBar.setTitle("Squirtle");
		_actionBar.setLogo(R.drawable.squirtle_icon);
		_imageView.setImageResource(R.drawable.squirtle);
		_textView.setText("After birth, its back swells and hardens into a shell. Powerfully sprays foam from its mouth.");
	}

    /**
     * Show Pikachu.
     */
	private void showPikachu()
	{
		_actionBar.setTitle("Pikachu");
		_actionBar.setLogo(R.drawable.pikachu_icon);
		_imageView.setImageResource(R.drawable.pikachu);
		_textView.setText("When several of these Pok�mon gather, their electricity could build and cause lightning storms.");
	}

    /**
     * Show no pokemon could be found.
     */
	private void showNotFound()
	{
		_actionBar.setTitle("No pok�mon found");
		_imageView.setImageResource(R.drawable.missingno);
		_textView.setText("No pok�mon was found, please try again.");
		_textView.setGravity(Gravity.CENTER_HORIZONTAL);
	}
}
