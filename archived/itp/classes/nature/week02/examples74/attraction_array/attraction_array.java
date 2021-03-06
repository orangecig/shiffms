import processing.core.*; import java.applet.*; import java.awt.*; import java.awt.image.*; import java.awt.event.*; import java.io.*; import java.net.*; import java.text.*; import java.util.*; import java.util.zip.*; public class attraction_array extends PApplet {int MAX = 5;
Thing[] t = new Thing[5];   //identical to the last program, but using an array instead
Attractor a;                //here we are creating an entirely separate object that exerts gravitational pull on other objects
boolean showVectors = true;

public void setup() {
  size(480,360);
  background(0);
  colorMode(RGB,255,255,255,100);
  for (int i = 0; i < t.length; i++) {
    Vector2D ac = new Vector2D(0.0f,0.0f);
    Vector2D ve = new Vector2D(random(-1,1),random(-1,1));
    Vector2D lo = new Vector2D(random(width),random(height));
    t[i] = new Thing(ac,ve,lo,random(8,16));
  }
  //create an attractive body
  a = new Attractor(new Vector2D(width/2,height/2),20,0.4f);
  smooth();
}

public void draw() {
  background(0);
  for (int i = 0; i < t.length; i++) {
    //calculate the force, the "attractor" exerts on the "thing"
    Vector2D f = a.calcGravForce(t[i]);
    //apply that force to the thing
    t[i].add_force(f);
    //update and render the positions of both objects
    t[i].go();
  }
  a.go();
}

public void mousePressed() {
  a.clicked(mouseX,mouseY);
}

public void mouseReleased() {
  a.stopDragging();
}

public void keyPressed() {
  showVectors = !showVectors;
}


// G ---> gravitational constant
// m1 --> mass of object #1
// m2 --> mass of object #2
// d ---> distance between objects
// F = (G*m1*m2)/(d*d)

class Attractor {
  float mass;    //Mass, tied to size
  float G;       //Gravitational Constant
  Vector2D loc;  //Location
  boolean dragging = false;
  Vector2D drag;  //holds the offset for when object is clicked on

  Attractor(Vector2D l_,float m_, float g_) {
    loc = l_;
    mass = m_;
    G = g_;
    drag = new Vector2D(0.0f,0.0f);
  }

  void go() {
    render();
    drag();
  }

  Vector2D calcGravForce(Thing t) {
    Vector2D dir = Vector2D.sub(loc,t.getLoc());      //calculate direction of force
    float d = dir.magnitude();                        //distance between objects
    d = constrain(d,20.0f,100.0f);                    //Limiting the distance to eliminate "extreme" results for very close or very far objects
    dir.normalize();                                  //normalize vector (distance doesn't matter here, we just want this vector for direction)
    float force = (G * mass * t.getMass()) / (d * d); //calculate gravitional force magnitude
    dir.mult(force);                                  //get force vector --> magnitude * direction
    return dir;
  }

  //function to display
  void render() {
    rectMode(CENTER);
    noStroke();
    if (dragging) {
      fill(255,0,0);
    } else {
      fill(0,255,0);
    }
    ellipse(loc.x(),loc.y(),mass*2,mass*2);
  }

  /*THE FUNCTIONS BELOW ARE FOR MOUSE INTERACTION*/
  void clicked(int mx, int my) {
    float d = dist(mx,my,loc.x(),loc.y());
    if (d < mass) {
      dragging = true;
      drag.setX(loc.x()-mx);
      drag.setY(loc.y()-my);
    }
  }

  void stopDragging() {
    dragging = false;
  }

  void drag() {
    if (dragging) {
      loc.setX(mouseX + drag.x());
      loc.setY(mouseY + drag.y());
    }
  }

}



// Force = Mass * Acceleration
// Acceleration = Force / Mass

/*A class to describe a thing in our world, has variables for location, velocity, and acceleration*/
/*now we add "mass" to this thing as well and a function to add forces*/

class Thing {
  Vector2D loc;
  Vector2D vel;
  Vector2D acc;
  float mass;
  float max_vel;

  //The Constructor (called when the object is first created)
  Thing(Vector2D a, Vector2D v, Vector2D l, float m_) {
    acc = a;
    vel = v;
    loc = l;
    mass = m_;
    max_vel = 5.0f;
  }

  /*we should probably make getters and setters for all of our instance variables
  but for this example, i'm just making the ones we are using*/
  Vector2D getLoc() {
    return loc;
  }

  float getMass() {
    return mass;
  }

  void add_force(Vector2D force) {
    acc.add(force);
  }

  void go() {
    update();
    render();
  }

  //function to update location
  void update() {
    vel.add(acc);
    vel.limit(max_vel);
    loc.add(vel);
    if (showVectors) drawVector(acc,loc,1000);        //we have to draw acceleration before we set it to 0    
    acc.setX(0.0f);
    acc.setY(0.0f);
  }

  //function to display
  void render() {
    if (showVectors) drawVector(vel,loc,50);
    rectMode(CENTER);
    noStroke();
    fill(0,0,255,75);
    rect(loc.x(),loc.y(),mass*2,mass*2);
  }

}



/*Our draw vector function*/
/*It might be nice to improve this to have different vectors colored differently*/
void drawVector(Vector2D v, Vector2D loc, float scayl) {
  push();
  float arrowsize = 10;
  //translate to location to render vector
  translate(loc.x(),loc.y());
  stroke(255);
  //call vector heading function to get direction (note that pointing up is a heading of 0) and rotate
  rotate(v.heading());
  //calculate length of vector & scale it to be bigger or smaller if necessary
  float len = v.magnitude()*scayl;
  //draw three lines to make an arrow (draw pointing up since we've rotate to the proper direction)
  line(0,0,len,0);
  line(len,0,len-arrowsize,+arrowsize/2);
  line(len,0,len-arrowsize,-arrowsize/2);
  pop();
}
}