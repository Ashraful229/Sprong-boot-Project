console.log("this is script file");

const toggleSidebar = () => {
	
  if ($(".sidebar").is(":visible")) {
    //true
    //band karna hai
    $(".sidebar").css("display", "none");
    $(".content").css("margin-left", "1%");
  } else {
    //false
    //show karna hai
    $(".sidebar").css("display", "block");
    $(".content").css("margin-left", "16%");
  }
};

const search=()=>{
	console.log("searching");
	
	let query=$("#search-input").val();

	if(query=='')
	{
			$(".search-result").hide();
	}
	else
	{
		console.log(query);
		//sending req to server
		let url= `http://localhost:8181/search/${query}`;
		fetch(url)
		.then((response)=>{
			return response.json();

          })
		.then((data)=>
		{
			console.log(data);
		  let text = `<div class='list-group'>`;
			data.forEach((contact)=>
			{
				text +=`<a href='/user/contact/${contact.cId}' class='list-group-item list-group-action'> ${contact.name} </a>`
			});
			
			
			text +=`</div>`;
			$(".search-result").html(text);
		
		   $(".search-result").show();
			  
		})
		
	}
}

