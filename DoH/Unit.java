import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Unit {

    public Coord position;
    public int lvl;
    public int maxHP;
    public int str;
    public int def;
    public int speed;
    public Class job;
    public int currHP;
    public boolean isDone;
    public TeamType team;
    public boolean isDead;
    
    public Coord offSet = new Coord(0, 0);
    public int animNumber = 0;
    
    public Unit(Coord pos, int level, int hp, int strength, int defense, int spd, Class type, TeamType side) {
        position = pos;
        lvl = level;
        maxHP = hp;
        currHP = hp;
        str = strength;
        def = defense;
        speed = spd;
        job = type;
        team = side;
        isDone = false;
        isDead = false;
    }

    public void move(Coord c) {
        // if (isValidMove(coord)) {
            position.moveTo(c);
        // }

    }

    public int expectedDamage(Unit enemy) {
        if (str - enemy.def <= 0) {
            return 0;
        }
        if (canAttackTwice(enemy)) {
            return (str - enemy.def)*2;               //Shows how much damage will get through defense
        }
        return str - enemy.def;
    }

    public boolean canAttackTwice(Unit enemy) {
        return speed/enemy.speed >= 2;
    }
    
    public void inflictDamage(int i) {
    	currHP -= i;
    	if (currHP <= 0) {
    		isDead = true;
    		currHP = 0;
    	}
    }

    public void battle(Unit enemy) {
        if (canAttack(enemy.position)) {
            enemy.currHP -= expectedDamage(enemy);
        }

        if (enemy.canAttack(position)) {
            currHP -= enemy.expectedDamage(this);
        }

    }

    public boolean canAttack(Coord c) {
		return position.distance(c) >= job.range[0] && position.distance(c) <= job.range[1];
	}

	public void setTeam(TeamType whichTeam) {
        team = whichTeam;
    }

    // public Wait() {

    // }

    // public boolean isValidMove(int[] coord) {
    //     int distanceAway = (coord[0] + coord[1] - 1) - (position[0] + position[1]);

    //     if (job.movement >= distanceAway) {
    //         return true;
    //     }
    //     return false;
    // }

    // public canAttack(int[] coord) {

    // }
    
    public BufferedImage image() {
    	try {
    		if (!isDone) {
    			return ImageIO.read(new File("src/" + team.name() + "/" + job.name() + ".PNG"));
    		} else {
    			return ImageIO.read(new File("src/HASMOVED/" + job.name() + ".PNG"));
    		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
    
    public String toString() {
        return job + " Lv: " + lvl + "\nHP: " + currHP + "/" + maxHP + "\nSTR: " + str +
            "\nDEF: " + def + "\nSPD: " + speed;
    }
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Unit other = (Unit) obj;
    	return this.toString().equals(other.toString());
    }
    
    public boolean animate(int frame, AnimationType o, Unit target) {
    	int frameNumber = frame % 30;
    	Coord dir = position.getDirection(target.position);
    	switch (o) {
    	case ATTACK:
    		if (frameNumber < 20) {
    			offSet.sub(dir);
    		} else {
    			offSet.add(dir);
    			offSet.add(dir);
    		} 
    		if (frameNumber == 29) {
    			offSet = new Coord(0, 0);
    			return true;
    		}
    	}
		return false;
    }

	public boolean animateWalk(Direction direction) {
		offSet.add(direction.toCoord());
		offSet.add(direction.toCoord());
		offSet.add(direction.toCoord());
		offSet.add(direction.toCoord());
		animNumber +=1;
		if (animNumber == 10) {
			animNumber = 0;
			offSet = new Coord(0, 0);
			return true;
		}
		return false;
	}
    
}



