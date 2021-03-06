#version 120

uniform struct {
	vec2 Angle;
	vec3 Position;
} Camera;

uniform vec3 Position;
uniform mat3 Heading;

mat4 ModelViewProjection = mat4(
	1, 0, 0, 0,
	0, 1, 0, 0,
	0, 0, 0, 1,
	0, 0, 1, 0
) * mat4(
	1, 0, 0, 0,
	0, cos(Camera.Angle.y), sin(Camera.Angle.y), 0,
	0, -sin(Camera.Angle.y), cos(Camera.Angle.y), 0,
	0, 0, 0, 1
) * mat4(
	cos(Camera.Angle.x), 0, -sin(Camera.Angle.x), 0,
	0, 1, 0, 0,
	sin(Camera.Angle.x), 0, cos(Camera.Angle.x), 0,
	0, 0, 0, 1
) * mat4(
	1, 0, 0, 0,
	0, 1, 0, 0,
	0, 0, 1, 0,
	-Camera.Position, 1
) * mat4(
	1, 0, 0, 0,
	0, 1, 0, 0,
	0, 0, 1, 0,
	Position, 1
) * mat4(Heading);

attribute vec4 in_Vertex;

void main() {
	gl_Position = ModelViewProjection * in_Vertex;
}
