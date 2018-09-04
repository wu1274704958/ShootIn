package org.sid.shootin.particle;


import org.sid.shootin.Vec2;

import java.util.ArrayList;
import java.util.Random;

public class ParticleGen {
    public static ArrayList<Part> ps;
    public static float R;


    public static void Gen(float r,float min_r,float max_r,int count)
    {
        R = r;
        ps = new ArrayList<>();
        for(int i = 0;i < count;++i)
        {
            float x = new Random().nextFloat() * (r * 2.f) - r;
            float y = new Random().nextFloat() * (r * 2.f) - r;

            float p_r = (new Random().nextFloat() * (max_r - min_r)) + min_r;

            double d = Math.sqrt(x*x + y * y) ;
            if(Math.abs( d - (r - p_r) ) <= 0.001)
            {
                ps.add(new Part(new Vec2(x,y),new Vec2(x ,y ),p_r));
            }
            if(d < r - p_r)
            {
                ps.add(new Part(new Vec2(x,y),new Vec2(x ,y ),p_r));
            }
        }
    }

}
