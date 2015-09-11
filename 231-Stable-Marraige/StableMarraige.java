package stablemarriage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StableMarriage {
	static Map<String, Man> men = new HashMap<String, Man>();
	static Map<String, Woman> women = new HashMap<String, Woman>();

	public static void main(String[] args) throws IOException {

		/*
		 * parse input
		 */
		for (String line : Files.readAllLines(Paths.get("input.txt"))) {
			String[] data = line.split(", ");
			String[] prefs = Arrays.copyOfRange(data, 1, data.length);
			String name = data[0];
			if (name.equals(name.toUpperCase())) { // is a man
				Man man = new Man(name);
				men.put(name, man);
				for (String womanName : prefs) {
					if (!women.containsKey(womanName)) {
						women.put(womanName, new Woman(womanName));
					}
					man.nextPreference(women.get(womanName));
				}
			} else { // is a woman
				if (!women.containsKey(name)) {
					women.put(name, new Woman(name));
				}
				Woman woman = women.get(name);
				for (String manName : prefs) {
					if (!men.containsKey(manName)) {
						men.put(manName, new Man(manName));
					}
					woman.nextPreference(men.get(manName));
				}
			}
		}

		/*
		 * main logic
		 */
		boolean done = false;
		while (!done) {
			boolean hasChanged = false;
			for (Man man : men.values()) {
				if (!man.isEnganged() && man.proposalsLeft()) {
					man.proposeToNext();
					hasChanged = true;
				}
			}
			done = !hasChanged;
		}

		/*
		 * output
		 */
		for (Man man : men.values()) {
			System.out.printf("(%s; %s)\n", man.getName(), man.getEngagedTo().getName());
		}
	}

}

class Person {
	protected List<Person> prefs = new ArrayList<Person>();
	protected Person engagedTo;
	private String name;

	Person(String name) {
		this.name = name;
	}

	void breakEngagement() {
		engagedTo = null;
	}

	Person getEngagedTo() {
		return engagedTo;
	}

	String getName() {
		return name;
	}

	boolean isEnganged() {
		return engagedTo != null;
	}

	void nextPreference(Person person) {
		prefs.add(person);
	}
}

class Man extends Person {
	private int nextProposal = 0;

	Man(String name) {
		super(name);
	}

	boolean proposalsLeft() {
		return nextProposal <= prefs.size(); // todo
	}

	void proposeToNext() {
		Woman woman = (Woman) prefs.get(nextProposal++);
		if (woman.acceptProposal(this)) {
			engagedTo = woman;
		}
	}
}

class Woman extends Person {

	Woman(String name) {
		super(name);
	}

	boolean acceptProposal(Man man) {
		if (isEnganged() && prefs.indexOf(man) > prefs.indexOf(engagedTo)) {
			return false; // already happily engaged
		} else {
			if (isEnganged()) {
				engagedTo.breakEngagement();
			}
			engagedTo = man;
			return true;
		}
	}
}
