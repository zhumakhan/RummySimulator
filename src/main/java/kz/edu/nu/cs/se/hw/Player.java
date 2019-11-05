package kz.edu.nu.cs.se.hw;

import java.util.*;

public class Player {
	Card fromDiscard;
	public String name;
	public List<Card> handCards = null;
	public Player(String name) {
		fromDiscard = new Card("");
		this.name = name;
		handCards = new ArrayList<Card>();
	}
}
