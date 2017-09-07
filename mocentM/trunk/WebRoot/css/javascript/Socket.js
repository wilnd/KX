//var Socket = function(host_ip, host_port){
//	
//	isConnected: false;
//	
//	socketClient: null;
//
//	receivedMessage: "";
//	
//	this.init = function(){
//		socketClient = new io.Socket(host_ip, { 
//			  port: host_port 
//		}); 
//		socketClient.connect(); 
//	}
//	
//	this.onSocketConnected = function(){
//		if(socketClient != null)
//		{
//			socket.on("conect", function(){
//				isConnected = true;
//			});
//		}
//	}
//	
//	this.onMessageReceived = function(){
//		if(isConnected)
//		{
//			socketClient.on("message", function(data){
//				receivedMessage = data;
//			});
//		}
//	}
//	
//	this.onSendMessage = function(message){
//		if(isConnected)
//		{
//			socketClient.send(message); 
//		}
//	}
//	
//	this.onSocketDisConnect = function(){
//		if(isConnected){
//			socketClient.on("disconnect", function(){
//				socketClient.close();
//			});
//		}
//	}
//	
//}

var gSocket;

function sendMessage()
{
    var othername = $("#othername").val();
    var msg= " MSG\t"+username+"_"+othername+"_"+$("#message").val();
    send(msg);
}

function send(data)
{
    console.log("Send:"+data);
    ws.send(data);
}

function startWebSocket()
{    
	var wsServer = "ws://120.25.94.112:9001"
	try 
	{
		if (!window.WebSocket) 
		{
			if (window.MozWebSocket) 
			{
				gSocket = new window.MozWebSocket(wsServer);
	        }
			else 
			{
				gSocket = new window.WebSocket(wsServer);
	        }
		}
	} 
	catch (e) {
	    //console.log(e);
	    return;
	}
    if (gSocket == undefined) {
        alert("不支持websocket");
        return;
    }
    
	gSocket.onopen = function(event){
		onSocketOpen(event)
    };
    gSocket.onmessage = function(event)
    {
         console.log("RECEIVE:" + event.data);
         handleData(event.data); 
    };
    gSocket.onclose = function(event) { 
    	onSocketClose(event)
    }; 
}

function onSocketOpen(event)
{
	var seq = getLoginSeq();
	send(seq);
}

function onSocketClose(event)
{
	console.log("Client notified socket has closed", event); 
}

function handleData(data)
{
	
}