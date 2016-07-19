
/**
 * Module dependencies.
 */
var ejs=require("ejs");
var express = require('express')
, routes = require('./routes')
, user = require('./routes/user')
, http = require('http')
, path = require('path');


var app = express();
var assert = require('assert');

var soap = require('soap');
var baseURL = "http://localhost:8080/TwitterSOAPServer/services";

var expressSession = require("express-session");
var searchTerm = "";

app.use(expressSession({  
	secret: 'cmpe273_teststring',  
	resave: false,  //don't save session if unmodified  
	saveUninitialized: false, // don't create session until something stored  
	duration: 30 * 60 * 1000,     
	expires: new Date(Date.now() + 3600000),
	activeDuration: 5 * 60 * 1000,
	cookie: {maxAge: 10 * 60 * 1000},
	}));

app.use(express.bodyParser());
app.use(express.cookieParser());


app.set('port', process.env.PORT || 3000);
app.set('views', __dirname + '/views');
app.set('view engine', 'ejs');
app.use(express.favicon());
app.use(express.logger('dev'));
app.use(express.bodyParser());
app.use(express.methodOverride());
app.use(app.router);
app.use(express.static(path.join(__dirname, 'public')));

//development only
if ('development' == app.get('env')) {
	app.use(express.errorHandler());
}


app.get('/users', user.list);
app.get('/', function(req,res){

	ejs.renderFile('./views/signin.ejs',function(err, result) {
		// render on success
		if (!err) {
			res.end(result);
		}
		// render or error
		else {
			res.end('An error occurred');
			console.log(err);
		}
	});
});

app.get('/home',function(req,res){

	ejs.renderFile('./views/successLogin.ejs',function(err, result) {
		// render on success
		if (!err) {
			res.end(result);
		}
		// render or error
		else {
			res.end('An error occurred');
			console.log(err);
		}
	});

});
app.post('/signin',function(req,res){

	var option = {
		ignoredNamespaces : true
	}

	var url = baseURL+"/login?wsdl";
	console.log(url);
	var args = {username: req.param("username"), password: req.param("password")};
	soap.createClient(url,option,function(err,client){
		client.mylogin(args,function(err,result){
			console.log(result);
			if(result.myloginReturn!==null){

				var data = JSON.parse(result.myloginReturn);
				sess = req.session;
				sess.username = data.username;
				res.send("Success");
				// res.redirect('/profile');
			}
			else{
				res.send("Fail");
			}
		});
	});


});


app.get('/loadPage', function(req,res){

	if(req.session.username) {
		var option = {
			ignoredNamespaces: true
		}

		var url = baseURL + "/loadPage?wsdl";
		console.log(url);
		var args = {username: req.session.username};
		soap.createClient(url, option, function (err, client) {
			client.loadpageProfile(args, function (err, result) {
				console.log(result);
				if (result.loadpageProfileReturn !== null) {
					console.log("in if");
					var data = JSON.parse(result.loadpageProfileReturn);
					res.send(data);
					// res.redirect('/profile');
				}
				else {
					res.send("Fail");
				}
			});
		});
	}
	else
	{
		console.log("homeloggingout")
		res.redirect('/');
	}
});

app.get('/followSuggestion', function(req,res){

	var option = {
		ignoredNamespaces : true
	}

	var url = baseURL+"/loadPage?wsdl";
	console.log(url);
	var args = {username: req.session.username};
	soap.createClient(url,option,function(err,client){
		client.fetchsuggestions(args,function(err,result){
			console.log(result);
			if(result.fetchsuggestionsReturn!==null){
				var data = JSON.parse(result.fetchsuggestionsReturn);
				res.send(data);
				// res.redirect('/profile');
			}
			else{
				res.send("Fail");
			}
		});

	});
		
});

app.post('/follow',function(req,res){

	var option = {
		ignoredNamespaces : true
	}

	var url = baseURL+"/loadPage?wsdl";
	console.log(url);
	var args = {username: req.session.username, thandle : req.param("thandle")};
	soap.createClient(url,option,function(err,client){
		client.followuser(args,option,function(err,result){
			console.log(result);
			if(result.followuserReturn!==null){
				var data = JSON.parse(result.followuserReturn);
				res.send(data);
				// res.redirect('/profile');
			}
			else{
				res.send("Fail");
			}
		});

	});
});

app.get('/fetchTweets', function(req,res){

	var option = {
		ignoredNamespaces : true
	}

	var url = baseURL+"/loadPage?wsdl";
	console.log(url);
	var args = {username: req.session.username};
	soap.createClient(url,option,function(err,client){
		client.fetchTweets(args,function(err,result){
			console.log(result);
			if(result.fetchTweetsReturn!==null){
				console.log("in if tweets");
				var data = JSON.parse(result.fetchTweetsReturn);
				res.send(data);
				// res.redirect('/profile');
			}
			else{
				res.send("Fail");
			}
		});

	});

});

app.get('/fetchMyTweets', function(req,res){

	var option = {
		ignoredNamespaces : true
	}

	var url = baseURL+"/loadPage?wsdl";
	console.log(url);
	var args = {username: req.session.username};
	soap.createClient(url,option,function(err,client){
		client.fetchMyTweets(args,function(err,result){
			console.log(result);
			if(result.fetchMyTweetsReturn!==null){
				console.log("in if tweets");
				var data = JSON.parse(result.fetchMyTweetsReturn);
				res.send(data);
				// res.redirect('/profile');
			}
			else{
				res.send("Fail");
			}
		});

	});

});

app.post('/searchmembers', function(req,res){

	searchTerm= req.param("searchTerm");
	res.send("Success");
	/*var option = {
		ignoredNamespaces : true
	}

	var url = baseURL+"/loadPage?wsdl";
	console.log(url);
	var args = {searchitem: req.param("searchTerm")};
	soap.createClient(url,option,function(err,client){
		client.searchMem(args,function(err,result){
			console.log(result);
			if(result.searchMemsReturn!==null){
				//var data = JSON.parse(result.searchMemReturn);
				res.send(result.searchMemReturn);
			}
			else{
				res.send("Failure");
			}
		});

	});*/
});

app.get('/searchTweets', function(req,res){
	console.log("insearch tweets");
	if(req.session.username){
		console.log("session valid");
		
		var viewpath=path.join(__dirname, './views', 'search.ejs');
		console.log(viewpath);
		
		
		ejs.renderFile(viewpath,function(err, result) {
			console.log("Hello");
			// render on success
			if (!err) {
				console.log("Not Error");
				
				res.end(result);
			}
			// render or error
			else {
				res.end('An error occurred');
				console.log(err);
			}
		});

		
	}
	else
		res.redirect('/');
	

});

app.post('/registerTweet',function(req,res){
	
	if(req.session.username){

		var url = baseURL+"/loadPage?wsdl";
		console.log(url);
		var args = {username: req.session.username, tweet : req.param("tweet") };
		soap.createClient(url,function(err,client){
			client.registerTweet(args,function(err,result){
				console.log(result);
				if(result.registerTweetReturn!==null){
					console.log("in if tweets");
					var data = (result.registerTweetReturn);
					res.send(data);
					// res.redirect('/profile');
				}
				else{
					res.send("Fail");
				}
			});

		});


	}
	else
		res.redirect('/');
});

app.post('/retweet', function(req,res){
	
	if(req.session.username)
	{
		var url = baseURL+"/loadPage?wsdl";
		console.log(url);
		var args = {username: req.session.username, tweet : req.param("tweet") };
		soap.createClient(url,function(err,client){
			client.reTweet(args,function(err,result){
				console.log(result);
				if(result.reTweetReturn!==null){
					console.log("in if tweets");
					var data = (result.reTweetReturn);
					res.send(data);
					// res.redirect('/profile');
				}
				else{
					res.send("Fail");
				}
			});

		});


	}
	else
	{
		console.log("homeloggingout")
		res.redirect('/');	
	}

});



app.post('/sessionChk', function(req,res){
	if(req.session.username)
	{
				var response = {};
				response.value="Success";
				res.send(response);
	}
	else
	{
		console.log("homeloggingout")
		res.redirect('/');	
	}
});

app.get('/profile',function(req,res){
	if(req.session.username)
	{
		console.log("in home.profile");
		
		var viewpath=path.join(__dirname, './views', 'profile.ejs');
		console.log(viewpath);
		ejs.renderFile(viewpath,function(err, result) {
			console.log("Hello");
			// render on success
			if (!err) {
				console.log("Not Error");
				res.end(result);
			}
			// render or error
			else {
				res.end('An error occurred');
				console.log(err);
			}
		});

	}//Session set when user Request our app via URL
	
	else
	{
		console.log("homeloggingout")
		res.redirect('/');	
	}
});

app.get('/logout', function (req,res){
	req.session.destroy();
	var viewpath=path.join(__dirname, './views', 'signin.ejs');
	console.log(viewpath);
	ejs.renderFile(viewpath,function(err, result) {
		console.log("Hello");
		// render on success
		if (!err) {
			console.log("Not Error");
			res.end(result);
		}
		// render or error
		else {
			res.end('An error occurred');
			console.log(err);
		}
	});
});

app.get('/fetchfollowing', function(req,res){
	
	if(req.session.username)
	{
		var option = {
			ignoredNamespaces : true
		}

		var url = baseURL+"/loadPage?wsdl";
		console.log(url);
		var args = {username: req.session.username};
		soap.createClient(url,option,function(err,client){
			client.fetchFollowing(args,function(err,result){
				console.log(result);
				if(result.fetchFollowingReturn!==null){
					console.log("in if tweets");
					var data = JSON.parse(result.fetchFollowingReturn);
					res.send(data);
					// res.redirect('/profile');
				}
				else{
					res.send("Fail");
				}
			});

		});

	}
	else
	{
		console.log("homeloggingout")
		res.redirect('/');	
	}

});



app.get('/fetchfollowers', function(req,res){
	if(req.session.username)
	{
		var option = {
			ignoredNamespaces : true
		}

		var url = baseURL+"/loadPage?wsdl";
		console.log(url);
		var args = {username: req.session.username};
		soap.createClient(url,option,function(err,client){
			client.fetchFollowers(args,function(err,result){
				console.log(result);
				if(result.fetchFollowersReturn!==null){
					var data = JSON.parse(result.fetchFollowersReturn);
					res.send(data);
					// res.redirect('/profile');
				}
				else{
					res.send("Fail");
				}
			});

		});

	}
	else
	{
		console.log("homeloggingout")
		res.redirect('/');	
	}


});
app.post('/profileUpdate',function(req,res){
	if(req.session.username)
	{

		var option = {
			ignoredNamespaces : true
		}

		var url = baseURL+"/loadPage?wsdl";
		console.log(url);
		var str=req.param("bday");
		var bday=str.substring(0,10);
		var args = {username: req.session.username, bday :bday, location : req.param("location"), contact :req.param("contact")};
		soap.createClient(url,option,function(err,client){
			client.updateInfo(args,function(err,result){
				console.log(result);
				if(result.updateInfoReturn!==null){
					//var data = JSON.parse(result.updateInfoReturn);
					res.send(result.updateInfoReturn);
					// res.redirect('/profile');
				}
				else{
					res.send("Failure");
				}
			});

		});

	}
	else
	{
		console.log("homeloggingout")
		res.redirect('/');
	}
});

app.get('/searchTweets', function(req,res){

	if(req.session.username)
	{
		console.log("in home.profile");

		var viewpath=path.join(__dirname, './views', 'search.ejs');
		console.log(viewpath);
		ejs.renderFile(viewpath,function(err, result) {
			console.log("Hello");
			// render on success
			if (!err) {
				console.log("Not Error");
				res.end(result);
			}
			// render or error
			else {
				res.end('An error occurred');
				console.log(err);
			}
		});

	}//Session set when user Request our app via URL

	else
	{
		console.log("homeloggingout")
		res.redirect('/');
	}

});

app.post('/popTweets', function(req,res){
	if(req.session.username){

		var option = {
			ignoredNamespaces : true
		}

		var url = baseURL+"/loadPage?wsdl";
		console.log(url);
		var args = {searchTerm : searchTerm};
		soap.createClient(url,option,function(err,client){
			client.searchResults(args,function(err,result){
				console.log(result);
				if(result.searchResultsReturn!==null){
					console.log("in if");
					var data = JSON.parse(result.searchResultsReturn);
					res.send(data);
					// res.redirect('/profile');
				}
				else{
					res.send("Fail");
				}
			});
		});
	}
	else
	{
		console.log("homeloggingout")
		res.redirect('/');	
	}


});

app.post('/signup', function(req,res){

	ejs.renderFile('./views/signup.ejs',function(err, result) {
		// render on success
		if (!err) {
			res.end(result);
		}
		// render or error
		else {
			res.end('An error occurred');
			console.log(err);
		}
	});
	
});


app.post('/aftersignup', function(req,res){

		var url = baseURL+"/loadPage?wsdl";
		console.log(url);
		var args = {username: req.param("uname"), password : req.param("password"), fname : req.param("Fname"), lname : req.param("lname"), handle : req.param("THandle") };
		soap.createClient(url,function(err,client){
			client.signup(args,function(err,result){
				console.log(result);
				if(result.signupReturn !=null){
					var data = (result.signupReturn);
					res.send(data);
					// res.redirect('/profile');
				}
				else{
					res.send("Failed Signup");
				}
			});

		});

});

http.createServer(app).listen(app.get('port'), function(){
	console.log('Express server listening on port ' + app.get('port'));
});
