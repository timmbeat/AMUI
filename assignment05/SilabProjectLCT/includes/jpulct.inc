DPUJava JPULCT
{
	Computer = {ASS};
	Index = 501;
	TreeviewGroup = "Lane Change Task";
	Class = de.uulm.silab.lct.JPULCT;
	x_pos = 0;
	laneIndex = 2;
};

DPUSGEObject LeftR
{
	Computer = {ASS};
	Index = 60;
	Object = "lct.left";
	X = 200.0;
	Y = 0.0;
	Z = 0.0;
	Show = 1;
};

DPUSGEObject StraightR
{
	Computer = {ASS};
	Index = 60;
	Object = "lct.straight";
	X = 200.0;
	Y = 0.0;
	Z = 0.0;
	Show = 1;
};

DPUSGEObject RightR
{
	Computer = {ASS};
	Index = 60;
	Object = "lct.right";
	X = 200.0;
	Y = 0.0;
	Z = 0.0;
	Show = 1;
	ColR = 1;
};

DPUSGEObject LeftL
{
	Computer = {ASS};
	Index = 60;
	Object = "lct.left";
	X = 200.0;
	Y = 0.0;
	Z = 0.0;
	Show = 1;
};

DPUSGEObject StraightL
{
	Computer = {ASS};
	Index = 60;
	Object = "lct.straight";
	X = 200.0;
	Y = 0.0;
	Z = 0.0;
	Show = 1;
};

DPUSGEObject RightL
{
	Computer = {ASS};
	Index = 60;
	Object = "lct.right";
	X = 200.0;
	Y = 0.0;
	Z = 0.0;
	Show = 1;
	ColR = 1;
};

Connections = 
{
	VDyn.X -> JPULCT.CarPosX,
	SCNX.LaneIdx -> JPULCT.LaneIndex,

	JPULCT.PosLeftX -> LeftL.X,
	JPULCT.PosLeftX -> StraightL.X,
	JPULCT.PosLeftX -> RightL.X,
	JPULCT.PosRightX -> LeftR.X,
	JPULCT.PosRightX -> StraightR.X,
	JPULCT.PosRightX -> RightR.X,

	JPULCT.PosLeftY -> LeftL.Y,
	JPULCT.PosLeftY -> StraightL.Y,
	JPULCT.PosLeftY -> RightL.Y,
	JPULCT.PosRightY -> LeftR.Y,
	JPULCT.PosRightY -> StraightR.Y,
	JPULCT.PosRightY -> RightR.Y,

	JPULCT.VisibleL -> LeftL.Show,
	JPULCT.VisibleL -> LeftR.Show,
	JPULCT.VisibleC -> StraightL.Show,
	JPULCT.VisibleC -> StraightR.Show,
	JPULCT.VisibleR -> RightL.Show,
	JPULCT.VisibleR -> RightR.Show

};