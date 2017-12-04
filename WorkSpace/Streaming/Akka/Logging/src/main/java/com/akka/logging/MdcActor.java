package com.akka.logging;

import java.util.HashMap;
import java.util.Map;

import akka.actor.UntypedActor;
import akka.event.DiagnosticLoggingAdapter;
import akka.event.Logging;

public class MdcActor extends UntypedActor {

	final DiagnosticLoggingAdapter log = Logging.getLogger(this);

	public void onReceive(Object message) {

		if (message instanceof String) {
			Map<String, Object> mdc;
			mdc = new HashMap<String, Object>();
			mdc.put("requestId", 1234);
			mdc.put("visitorId", 5678);
			log.setMDC(mdc);
			log.info("Starting new request");

			log.clearMDC();
		}
	}
}