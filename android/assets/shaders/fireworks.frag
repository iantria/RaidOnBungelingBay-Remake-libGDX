#ifdef GL_ES
    #define PRECISION highp
    precision PRECISION float;
    precision PRECISION int;
#else
    #define PRECISION
#endif

uniform float u_time;
uniform float u_aspect_ratio;
varying vec2 v_texCoords;

vec4 NNN(float h) {
    return fract(sin(vec4(6.0,9.0,1.0,0.0)*h) * 900.0);
}

void main() {
    vec2 u = v_texCoords*u_aspect_ratio;
    vec4 o, p = vec4(0.0);
    float e, d, ii = -2.0;

    for(float i = -2.0; i < 9.0; i++) {
        ii = i;
        d = floor(e = i * 9.1 + u_time);
        p = NNN(d) + 0.3;
        e -= d;
        for(float d = 0.0; d < 50.0; d++) {
            o += p * (1.0 - e) / 1000.0 / length(u - (p - e*(NNN(d*i)-0.5)).xy);
        }
    }

    if(u.y<NNN(ceil(u.x * ii + d + e)).x * 0.4) o -= o * u.y;

    gl_FragColor = o;
   }