<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet"
	href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>
<script
	src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
<link href="style.css" rel="stylesheet" type="text/css">
<title>Registration Form</title>

</head>
<body>

	<form>
		<div style="width: 100%; height: 1000px;">
			<fieldset>
				<div style="float: top; width: 100%; height: 50px;">
					<h1 align="center">REGISTRATION FORM</h1>
				</div>
			</fieldset>
			<fieldset>
				<legend>personal details</legend>
				<div style="width: 100%; height: 600px;">
					

						<div style="float: left; width: 60%;">
							<div class="a">
								<div class="l">FirstName :</div>
								<div class="r">
									<input type="text" id="fname" />
								</div>
							</div>

							<div class="a">
								<div class="l">MiddleName :</div>
								<div class="r">
									<input type="text" id="mname" />
								</div>
							</div>
							<div class="a">
								<div class="l">UniqueId :</div>
								<div class="r">
									<input type="text" id="uid" />
								</div>
							</div>

							<div class="a">
								<div class="l">Gender:</div>
								<div class="r">
									<input type="radio" name="gender" value="male" checked>Male
									<input type="radio" name="gender" value="female">Female
								</div>
							</div>

						</div>

						<div style="float: right; width: 40%;">
							<div class="a">
								<div class="l">LastName :</div>
								<div class="r">
									<input type="text" id="lname" />
								</div>
							</div>
							<div class="a">
								<div class="l">DateOfBirth:</div>
								<div class="r">
									<input type="text" id="dob" />
								</div>
							</div>
							<div class="a">
								<div class="l">Pan Number :</div>
								<div class="r">
									<input type="text" id="pan" />
								</div>
							</div>
						</div></div></fieldset></div></form></body></html>
