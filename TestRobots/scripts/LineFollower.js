importPackage(Packages.it.uniroma1.di.simulejos.math);

var wheelSpan = 2;
var wheelDiameter = 1;

robot.S1.lightSensor(new Vector3(0, -0.8, 0.5), Matrix3.create([1, 0, 0, 0, -1, 0, 0, 0, 1]));

function tick(daa, dab, dac) {
	var dsa = daa * wheelDiameter * Math.PI;
	var dsb = dab * wheelDiameter * Math.PI;
	if (dsa != dsb) {
		var radius = wheelSpan * (dsa + dsb) / (2 * (dsb - dsa));
		var angle = (dab - daa) * Math.PI;
		robot.moveBy(radius * (Math.cos(angle) - 1), 0, radius * Math.sin(angle));
		robot.rotateBy(0, 1, 0, angle);
	} else {
		robot.moveBy(0, 0, dsa);
	}
}
