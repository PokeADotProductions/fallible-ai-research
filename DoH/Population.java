import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;


public class Population {
	
	int size;
	double killThresh;
	double mateThresh;
	double muteThresh;
	double mateAmount;
	ArrayList<Gene> genes;
	Levels levels = new Levels();
	Gene bestSoFar = new Gene();
	
	public Population(int siz, double killThres, double mateThres, double muteThres, double mateAmoun) {
		size = siz;
		killThresh = killThres;
		mateThresh = mateThres;
		muteThresh = muteThres;
		mateAmount = mateAmoun;
		genes = new ArrayList<Gene>();
		for (int i = 0; i < size; i ++) {
			genes.add(new Gene());
		}
	}
	
	public void sort() {
		Collections.sort(genes);
	}
	
	public void kill() {
		int end = (int) (killThresh * (double)size);
		for (int i = 0; i < end; i ++) {
			genes.remove(i);
		}
	}
	
	public void mutate() {
		int end = (int) (muteThresh * (double)size);
		for (int i = 0; i < end; i ++) {
			genes.get(new Random().nextInt(genes.size()));
		}
	}
	
	public void mate() {
		ArrayList<Gene> temp = new ArrayList<Gene>();
		int start = (int) (genes.size() * mateThresh);
		
		for (int i = 0; i < (int) (mateAmount * genes.size()); i ++) {
			ArrayList<Gene> poss = new ArrayList<Gene>();
			for (int j = start; j < genes.size(); j ++) {
				poss.add(genes.get(i));
			}
			int index = new Random().nextInt(poss.size());
			Gene mum = poss.get(index);
			poss.remove(index);
			index = new Random().nextInt(poss.size());
			Gene dad = poss.get(index);
			temp.add(mum.mate(dad));
		}
		for (Gene g : temp) {
			genes.add(g);
		}
	}
	
	public void refreshPopulation() {
		while (genes.size() < size) {
			genes.add(new Gene());
		}
	}
	
	public void resetFitness() {
		for (Gene g : genes) {
			g.fitness = 0;
		}
	}
	
	public double playMap (GameBoard gb, Player p) {
		gb.playerController = p;
		Game g = new Game(gb);
		int initHP = 0;
		for (Unit u : gb.enemy.units) {
			initHP += u.maxHP;
		}
		
		g.playGame(false);
		
		int finalHP = 0;
		for (Unit u : gb.enemy.units) {
			finalHP += u.currHP;
		}
		System.out.println("		" + initHP);
		System.out.println("		" + finalHP);
		System.out.println("		" + (finalHP - initHP));
		if (initHP == finalHP) {
			return -10;
		}
		return initHP - finalHP;
	}
	
	public void assess() {
		Gene ge = new Gene();
		genes.set(0, ge);
		for (int i = 0; i < genes.size(); i ++) {
			System.out.println(genes.get(i));

			genes.get(i).fitness += playMap(new Levels().getTestLevel(0), new Zion(genes.get(i)));
			System.out.println(genes.get(i).fitness);
		}
		for (int i = 0; i < genes.size(); i ++) {
			System.out.println(genes.get(i).fitness);
		}
		
		sort();
		if (genes.get(genes.size() - 1).fitness > bestSoFar.fitness) {
			bestSoFar = genes.get(genes.size() - 1).copy();
		}
	}
	
	private int randomInt(int min, int max) {
		return new Random().nextInt(max - min) + min;
	}
	
	public String toString() {
		String s = "";
		for (Gene g : genes) {
			s += g.toString() + "\n";
		}
		s += "Best So Far: " + bestSoFar.toString() + "\n";
		return s;
	}
	
	public static void main(String[] args) {
		Population pop = new Population(2, .2, .2, .2, .2);
		try {
			PrintWriter writer = new PrintWriter("src/data", "UTF-8");
			for (int i = 0; i < 2; i ++) {
				writer.println("Gen " + i);
				pop.assess();
				writer.println(pop.toString());
				pop.kill();
				pop.mate();
				pop.refreshPopulation();
				pop.mutate();
				pop.resetFitness();
			}
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}




