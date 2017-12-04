<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Registration</title>

<link rel="stylesheet"
	href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>
<script
	src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>

<style>
fieldset.scheduler-border {
	border: 1px groove #ddd;
	padding: 0 1.4em 1.4em 1.4em;
	margin: 0 0 1.5em 0;
	-webkit-box-shadow: 0px 0px 0px 0px #000;
	box-shadow: 0px 0px 0px 0px #000;
	margin: 0 0 1.5em 0;
}

legend.scheduler-border {
	width: inherit;
	padding: 0 10px;
	border-bottom: none;
}
</style>

</head>
<body>

	<div class="container"
		style="width: 100%; height: 1000px; border: 5px black solid">
		<div>
			<legend>
				<h1>
					<center>Registration Form</center>
				</h1>
			</legend>
		</div>

		<div class="row well">
			<form role="form" action="">
				<fieldset class="scheduler-border">
					<legend class="scheduler-border">Personal Details</legend>

					<div class="form-group row col-xs-12">
						<label for="rnumber" class="col-sm-3 form-control-label">Registration
							Number </label>
						<div class="col-sm-2">
							<input type="text" class="form-control" id="rnumber">
						</div>
						<div class="col-sm-2">
							<a href="#" class="btn btn-default btn-xs">genarate</a>
						</div>
					</div>


					<div class="form-group row col-xs-6">
						<label for="fname" class="col-sm-6 form-control-label">First
							Name </label>
						<div class="col-sm-6">
							<input type="text" class="form-control" id="fname"
								placeholder="First Name">
						</div>
					</div>

					<div class="form-group row col-xs-6">
						<label for="lname" class="col-sm-6 form-control-label">Last
							Name</label>
						<div class="col-sm-6">
							<input type="text" class="form-control" id="lname"
								placeholder="Last Name">
						</div>
					</div>

					<div class="form-group row col-xs-6">
						<label for="mname" class="col-sm-6 form-control-label">Middle
							Name</label>
						<div class="col-sm-6">
							<input type="text" class="form-control" id="mname"
								placeholder="Middle Name">
						</div>
					</div>

					<div class="form-group row col-xs-6">
						<label for="dob" class="col-sm-6 form-control-label">Date
							Of Birth</label>
						<div class="col-sm-6">
							<input type="text" class="form-control" id="dob"
								placeholder="Date Of Birth">
						</div>
					</div>

					<div class="form-group row col-xs-6">
						<label for="uid" class="col-sm-6 form-control-label">Unique
							Id</label>
						<div class="col-sm-6">
							<input type="text" class="form-control" id="uid"
								placeholder="Unique Id">
						</div>
					</div>

					<div class="form-group row col-xs-6">
						<label for="pan" class="col-sm-6 form-control-label">PAN
							Number</label>
						<div class="col-sm-6">
							<input type="text" class="form-control" id="pan"
								placeholder="PAN Number">
						</div>
					</div>

					<div class="form-group row col-xs-6">
						<label for="gender" class="col-sm-6 form-control-label">Gender</label>
						<div class="col-sm-6">
							<label> <input type="radio" name="gender" id="male"
								value="option1"> Male
							</label> <label> <input type="radio" name="gender" id="female"
								value="option2"> Female
							</label>
						</div>
					</div>

					<div class="form-group row col-xs-6">
						<label for="status" class="col-sm-6 form-control-label">Marital
							Status</label>
						<div class="col-sm-6">
							<select class="form-control" id="sel1">
								<option>Single</option>
								<option>Married</option>
								<option>Separated</option>
								<option>Divorced</option>
								<option>Widowed</option>
							</select>
						</div>
					</div>

					<div class="form-group row col-xs-12">
						<label for="caddress" class="col-sm-3 form-control-label">Communication
							Address </label>
						<div class="col-sm-8">
							<textarea class="form-control" rows="3" id="caddress"
								style="width: 735px"></textarea>
						</div>
					</div>

					<div class="form-group row col-xs-12">
						<label class="col-sm-3 form-control-label"> </label>
						<div class="col-sm-8">
							<input type="checkbox"> Same as Communication Address
						</div>

					</div>

					<div class="form-group row col-xs-12">
						<label for="paddress" class="col-sm-3 form-control-label">Parmanent
							Address </label>
						<div class="col-sm-8">
							<textarea class="form-control" rows="3" id="paddress"
								style="width: 735px"></textarea>
						</div>
					</div>
				</fieldset>
			</form>
		</div>

		<div class="row well">
			<form action="">
				<fieldset class="scheduler-border">
					<legend class="scheduler-border">Education Details</legend>
					<table class="table">
						<thead>
							<tr>
								<td>Course</td>
								<td>University/College</td>
								<td>Year of pass</td>
								<td>Percentage</td>
								<td>
									<button type="button" class="btn btn-default btn-sm">
										<span class="glyphicon glyphicon-plus"></span>
									</button>
								</td>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td><input type="text" name="course"></td>
								<td><input type="text" name="college"></td>
								<td><input type="text" name="yop"></td>
								<td><input type="text" name="percentage"> <span>%</span>
								</td>
								<td>
									<button type="button" class="btn btn-default btn-sm">
										<span class="glyphicon glyphicon-minus"></span>
									</button>
								</td>
							</tr>
							<tr>
								<td><input type="text" name="course"></td>
								<td><input type="text" name="college"></td>
								<td><input type="text" name="yop"></td>
								<td><input type="text" name="percentage"> <span>%</span>
								</td>
								<td>
									<button type="button" class="btn btn-default btn-sm">
										<span class="glyphicon glyphicon-minus"></span>
									</button>
								</td>
							</tr>
						</tbody>
					</table>
				</fieldset>
			</form>
		</div>
	</div>

</body>
</html>
