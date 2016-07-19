package infoObj;

public class homeprofile {
	String fullname; 
	String thandle;
	int numFollowers;
	int numFollowing;
	int numTweets;
	
	
	public homeprofile(String name,String handle,	int followers, int following,	int tweets){
		fullname=name;
		thandle=handle;
		numFollowers= followers;
		numFollowing=following;
		numTweets=tweets;
	}

}
