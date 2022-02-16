#ifdef GL_ES
    #define PRECISION highp
    precision PRECISION float;
    precision PRECISION int;
#else
    #define PRECISION
#endif

uniform float u_time;
//uniform float u_aspect_ratio;
uniform vec2 u_resolution;
varying vec2 v_texCoords;

const float PI	 	= 3.14159265358;

// Can you explain these epsilons to a wide graphics audience?  YOUR comment could go here.
const float EPSILON	= 1.0e-3;
#define  EPSILON_NRM	(0.5 / u_resolution.x)

// Constant indicaing the number of steps taken while marching the light ray.
const int NUM_STEPS = 6;

//Constants relating to the iteration of the heightmap for the wave, another part of the rendering process.
const int ITER_GEOMETRY = 2;
const int ITER_FRAGMENT = 5;

// Constants that represent physical characteristics of the sea, can and should be changed and played with
const float SEA_HEIGHT = 0.5;
const float SEA_CHOPPY = 3.0;
const float SEA_SPEED = 1.9;
const float SEA_FREQ = 0.24;
const vec3 SEA_BASE = vec3(0.11,0.19,0.22);
const vec3 SEA_WATER_COLOR = vec3(0.55,0.9,0.7);
#define SEA_time (u_time * SEA_SPEED)

//Matrix to permute the water surface into a complex, realistic form
mat2 octave_m = mat2(1.7,1.2,-1.2,1.4);

//This one converts red-green-blue color to hue-saturation-value color
vec3 rgb2hsv(vec3 c) {
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

//This one converts hue-saturation-value color to red-green-blue color
vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}


// bteitler: Turn a vector of Euler angles into a rotation matrix
mat3 fromEuler(vec3 ang) {
	vec2 a1 = vec2(sin(ang.x),cos(ang.x));
    vec2 a2 = vec2(sin(ang.y),cos(ang.y));
    vec2 a3 = vec2(sin(ang.z),cos(ang.z));
    mat3 m;
    m[0] = vec3(a1.y*a3.y+a1.x*a2.x*a3.x,a1.y*a2.x*a3.x+a3.y*a1.x,-a2.y*a3.x);
	m[1] = vec3(-a2.y*a1.x,a1.y*a2.y,a2.x);
	m[2] = vec3(a3.y*a1.x*a2.x+a1.y*a3.x,a1.x*a3.x-a1.y*a3.y*a2.x,a2.y*a3.y);
	return m;
}

// Performance is a real consideration of hash functions since ray-marching is already so heavy.
float hash( vec2 p ) {
    float h = dot(p,vec2(127.1,311.7));
    return fract(sin(h)*83758.5453123);
}

float noise( in vec2 p ) {
    vec2 i = floor( p );
    vec2 f = fract( p );
    vec2 u = f*f*(3.0-2.0*f);
    return -1.0+2.0*mix(
                mix( hash( i + vec2(0.0,0.0) ),
                     hash( i + vec2(1.0,0.0) ),
                        u.x),
                mix( hash( i + vec2(0.0,1.0) ),
                     hash( i + vec2(1.0,1.0) ),
                        u.x),
                u.y);
}

// bteitler: diffuse lighting calculation - could be tweaked to taste lighting
float diffuse(vec3 n,vec3 l,float p) {
    return pow(dot(n,l) * 0.4 + 0.6,p);
}

// bteitler: specular lighting calculation - could be tweaked taste
float specular(vec3 n,vec3 l,vec3 e,float s) {
    float nrm = (s + 8.0) / (3.1415 * 8.0);
    return pow(max(dot(reflect(e,n),l),0.0),s) * nrm;
}

// bteitler: Generate a smooth sky gradient color based on ray direction's Y value sky
vec3 getSkyColor(vec3 e) {
    e.y = max(e.y,0.0);
    vec3 ret;
    ret.x = pow(1.0-e.y,2.0);
    ret.y = 1.0-e.y;
    ret.z = 0.6+(1.0-e.y)*0.4;
    return ret;
}

// sea
float sea_octave(vec2 uv, float choppy) {
    uv += noise(uv);
    vec2 wv = 1.0-abs(sin(uv));
    vec2 swv = abs(cos(uv));
    wv = mix(wv,swv,wv);
    return pow(1.0-pow(wv.x * wv.y,0.65),choppy);
}

float map(vec3 p) {
    float freq = SEA_FREQ;
    float amp = SEA_HEIGHT;
    float choppy = SEA_CHOPPY;
    vec2 uv = p.xz; uv.x *= 0.75;

    float d, h = 0.0;
    for(int i = 0; i < ITER_GEOMETRY; i++) {
    	d = sea_octave((uv+SEA_time)*freq,choppy);
        h += d * amp; // bteitler: Bump our height by the current wave function
    	uv *=  octave_m;
        freq *= 1.9; // bteitler: Exponentially increase frequency every iteration (on top of our permutation)
        amp *= 0.22; // bteitler: Lower the amplitude every frequency, since we are adding finer and finer detail
        choppy = mix(choppy,1.0,0.2);
    }
    return p.y - h;
}

float map_detailed(vec3 p) {
    float freq = SEA_FREQ;
    float amp = SEA_HEIGHT;
    float choppy = SEA_CHOPPY;
    vec2 uv = p.xz; uv.x *= 0.75;

    float d, h = 0.0;
    for(int i = 0; i < ITER_FRAGMENT; i++) {
    	d = sea_octave((uv+SEA_time)*freq,choppy);
    	d += sea_octave((uv-SEA_time)*freq,choppy);
        h += d * amp; // bteitler: Bump our height by the current wave function
    	uv *= octave_m/1.2;
        freq *= 1.9; // bteitler: Exponentially increase frequency every iteration (on top of our permutation)
        amp *= 0.22; // bteitler: Lower the amplitude every frequency, since we are adding finer and finer detail
        choppy = mix(choppy,1.0,0.2);
    }
    return p.y - h;
}

vec3 getSeaColor(vec3 p, vec3 n, vec3 l, vec3 eye, vec3 dist) {
    float fresnel = 1.0 - max(dot(n,-eye),0.0);
    fresnel = pow(fresnel,3.0) * 0.45;
    vec3 reflected = getSkyColor(reflect(eye,n))*0.99;
    vec3 refracted = SEA_BASE + diffuse(n,l,80.0) * SEA_WATER_COLOR * 0.27;
    vec3 color = mix(refracted,reflected,fresnel);
    float atten = max(1.0 - dot(dist,dist) * 0.001, 0.0);
    color += SEA_WATER_COLOR * (p.y - SEA_HEIGHT) * 0.15 * atten;
    color += vec3(specular(n,l,eye,90.0))*0.5;
    return color;
}


vec3 getNormal(vec3 p, float eps) {
    vec3 n;
    n.y = map_detailed(p); // bteitler: Detailed height relative to surface, temporarily here to save a variable?
    n.x = map_detailed(vec3(p.x+eps,p.y,p.z)) - n.y; // bteitler approximate X gradient as change in height along X axis delta
    n.z = map_detailed(vec3(p.x,p.y,p.z+eps)) - n.y; // bteitler approximate Z gradient as change in height along Z axis delta
    n.y = eps;
    return normalize(n);
}




// bteitler: Find out where a ray intersects the current ocean
float heightMapTracing(vec3 ori, vec3 dir, out vec3 p) {
    float tm = 0.0;
    float tx = 500.0; // bteitler: a really far distance, this could likely be tweaked a bit as desired
    float hx = map(ori + dir * tx);
    if(hx > 0.0) return tx;
    float hm = map(ori + dir * tm);
    float tmid = 0.0;
    for(int i = 0; i < NUM_STEPS; i++) { // bteitler: Constant number of ray marches per ray that hits the water
        tmid = mix(tm,tx, hm/(hm-hx));
        p = ori + dir * tmid;
    	float hmid = map(p); // bteitler: Re-evaluate height relative to ocean surface in Y axis

        if(hmid < 0.0) { // bteitler: We went through the ocean surface if we are negative relative to surface now
            tx = tmid;
            hx = hmid;
        } else {
            tm = tmid;
            hm = hmid;
        }
    }
    return tmid;
}

// main
void main() {
    vec2 uv = v_texCoords.xy;

    uv = uv * 2.0 - 1.0; //  bteitler: Shift pixel coordinates from 0 to 1 to between -1 and 1
    uv.x *= u_resolution.x / u_resolution.y; // bteitler: Aspect ratio correction - if you don't do this your rays will be distorted
    float time = u_time * 2.7; // bteitler: Animation is based on time, but allows you to scrub the animation based on mouse movement

    float roll = PI + sin(u_time)/14.0 + cos(u_time/2.0)/14.0 ;
    float pitch = PI*1.021 + (sin(u_time/2.0)+ cos(u_time))/40.0;
    float yaw = u_resolution.x * PI * 4.0;
    vec3 ang = vec3(roll,pitch,yaw);

    vec3 ori = vec3(0.0,3.5,time*3.0);
    vec3 dir = normalize(vec3(uv.xy,-1.6));
    dir = normalize(dir) * fromEuler(ang);

    vec3 p;
    heightMapTracing(ori,dir,p);

    vec3 dist = p - ori; // bteitler: distance vector to ocean surface for this pixel's ray

    vec3 n = getNormal(p,
             dot(dist,dist)   // bteitler: Think of this as inverse resolution, so far distances get bigger at an expnential rate
                * EPSILON_NRM // bteitler: Just a resolution constant.. could easily be tweaked to artistic content
           );

    // bteitler: direction of the infinitely far away directional light.  Changing this will change
    // the sunlight direction.
    vec3 light = normalize(vec3(0.0,1.0,0.8));

    // CaliCoastReplay:  Get the sky and sea colors
	vec3 skyColor = getSkyColor(dir);
    vec3 seaColor = getSeaColor(p,n,light,dir,dist);

    seaColor /= sqrt(sqrt(length(dist))) ;

    bool night = false;
   //Brighten the sea up again - bright and beautiful blue at day
    	seaColor *= sqrt(sqrt(seaColor)) * 4.0;
        skyColor *= 1.05;
        skyColor -= 0.03;
        night = false;

    //CaliCoastReplay:  A slight "constrasting" for the sky to match the more contrasted ocean
    skyColor *= skyColor;

    vec3 seaHsv = rgb2hsv(seaColor);
    if (seaHsv.z > .75 && length(dist) < 50.0)
       seaHsv.z -= (0.9 - seaHsv.z) * 1.3;
    seaColor = hsv2rgb(seaHsv);

    vec3 color = mix(
        skyColor,
        seaColor,
    	pow(smoothstep(0.0,-0.05,dir.y), 0.3) // bteitler: Can be thought of as "fog" that gets thicker in the distance
    );

    vec4 fragColor = vec4(pow(color,vec3(0.75)), 1.0);

    vec3 hsv = rgb2hsv(fragColor.xyz);
    hsv.y += 0.131;
    hsv.z *= sqrt(hsv.z) * 1.1;

    if (night)    {
        hsv.z -= 0.045;
        hsv*=0.8;
        hsv.x += 0.12 + hsv.z/100.0;
        //Highly increased saturation at night op, oddly.  Nights appear to be very colorful
        //within their ranges.
        hsv.y *= 2.87;
    }  else    {
        hsv.z *= 0.9;
        hsv.x -= hsv.z/10.0;
        hsv.x += 0.02 + hsv.z/50.0;
        hsv.z *= 1.01;
        hsv.y += 0.07;
    }

    fragColor.xyz = hsv2rgb(hsv);
    gl_FragColor = fragColor;
}