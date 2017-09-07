package mocent.Monitor.SocketProxy;

import java.util.ArrayList;
import java.util.List;

import com.google.common.eventbus.EventBus;

public class EventManager{
	private static EventManager _instanse;
	
	private int[] mCmds;
	
	private Object mSubScriber;
	
	public static EventBus eventBus;
	
	List<BaseEvent> mEventsList = new ArrayList<BaseEvent>();
	
	private EventManager(){
	
	}
	
	static{
		eventBus = new EventBus("EventManager");
	}
	
	public static EventManager getInstance()
	{
		if(_instanse == null)
		{
			_instanse = new EventManager();
		}
		return _instanse;
	}
	
	public void register(Object subscriber, int[] cmds)
	{
		this.mSubScriber = subscriber;
		this.mCmds = cmds;
		eventBus.register(subscriber);
		if(mCmds != null && mCmds.length > 0)
		{
			if(mCmds[0] == BaseEvent.EVENT_ALL)
			{
				synchronized (mEventsList) {
					for(BaseEvent ev: mEventsList)
					{
						eventBus.post(ev);
					}
					mEventsList.clear();
				}
			}
			else
			{
				List<BaseEvent> mFoundList = new ArrayList<BaseEvent>();
				synchronized (mEventsList) 
				{
					for(BaseEvent ev : mEventsList)
					{
						for(int i = 0; i < mCmds.length;i++)
						{
							if(ev.get_event_id() == mCmds[i])
							{
								eventBus.post(ev);
								mFoundList.add(ev);
								break;
							}
						}
					}
					mEventsList.removeAll(mFoundList);
				}
			}
		}
		
	}
	
	public void unregister(Object subscriber)
	{
		eventBus.unregister(subscriber);
		if(subscriber!=null && subscriber.equals(mSubScriber))
		{
			mSubScriber = null;
			mCmds = null;
		}
	}
	
	public void sendEvent(BaseEvent event)
	{
		if(mCmds!=null && mCmds.length > 0)
		{
			if(mCmds[0] == BaseEvent.EVENT_ALL){
				
			}
			else
			{
				boolean found = false;
				for(int i = 0; i < mCmds.length;i++){
					if(event.get_event_id() == mCmds[i])
					{
						eventBus.post(event);
						found = true;
						break;
					}
				}
				if(!found)
				{
					synchronized (mEventsList) {
						mEventsList.add(event);
					}
				}
			}
		}
		else
		{
			synchronized (mEventsList) 
			{
				mEventsList.add(event);
			}
		}
	}
}