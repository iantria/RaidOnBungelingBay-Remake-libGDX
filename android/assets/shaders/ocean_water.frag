#ifdef GL_ES
    #define PRECISION highp
    precision PRECISION float;
    precision PRECISION int;
#else
    #define PRECISION
#endif

uniform float u_time;
varying vec2 v_texCoords;

#define GRID_HEIGHT 10

const vec3 waterCol=vec3(0.0,0.44,0.8);
const vec3 highlightCol=vec3(0.5,0.8,1.0);
const float p=3.0;

float rand(vec2 pos)
{
    return fract(sin(dot(pos, vec2(12.9898, 78.233))) * 43758.5453);
}

float get_voronoi_noise(vec2 pos)
{
    float dist=2.0;
    for(int y=-1;y<=1;y++)
    {
        for(int x=-1;x<=1;x++)
        {
            vec2 gridP = vec2(floor(pos.x+float(x)),floor(pos.y+float(y)));
            vec2 rP = gridP+vec2(rand(gridP),rand(gridP.yx));
            vec2 offset=vec2(sin(u_time*6.0*rand(gridP)),cos(u_time*9.0*rand(gridP.yx)))*0.1;
            float dis=distance(pos,rP+offset);
            if(dis<dist)
            {
                dist=dis;
            }
        }
    }
    return dist/sqrt(2.0);
}

void main()
{
    // Gets UVs
    vec2 uv=v_texCoords;
    uv*=float(GRID_HEIGHT);

    // Gets Offser
    vec2 offset=vec2(0.0);
    offset.y=-u_time*-1.0;
    offset.x=cos(u_time*0.5)*-0.67;

    // Get Height Map
    float h=get_voronoi_noise(uv+offset) + get_voronoi_noise(uv+offset*0.4);
    h=clamp(h*0.7,0.0,1.0);
    h=pow(h,p);

    // Final Color
    vec3 col=vec3(h)*highlightCol+waterCol;

    // Output to screen
    gl_FragColor = vec4(col,1.0);
}
