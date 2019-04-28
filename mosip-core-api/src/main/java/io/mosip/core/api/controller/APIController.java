package io.mosip.core.api.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jayway.jsonpath.ReadContext;

import io.mosip.core.api.dao.ResidentRepo;
import io.mosip.core.api.dao.VINRepo;
import io.mosip.core.api.model.Resident;
import io.mosip.core.api.response.AddressResponse;
import io.mosip.core.api.response.UINReprintResponse;
import io.mosip.core.api.response.VINGeneration;
import io.mosip.core.api.response.VINRevoke;

@RestController
@RequestMapping("/mosip")
public class APIController {
	
	@Autowired
	VINRepo vinrepo;
	
	@Autowired
	ResidentRepo residentrepo;
	
	SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	@RequestMapping("/test")
	public String test() {
		return "Working";
	}
	
	
	@RequestMapping(value="/vin/generate" ,method=RequestMethod.POST)
	@ResponseBody
	public VINGeneration genVINPOST(@RequestBody JSONObject vinG) {
		ReadContext ctx = com.jayway.jsonpath.JsonPath.parse(vinG);
		String uin = ctx.read("$['request']['identity']['uin']");
		//String uin=vinG.getUin();
		//System.out.println("from server :"+uin);
		//JSONObject response_json = new JSONObject();
		if(uin==null||uin.equalsIgnoreCase("NA")) {
			/*
			response_json.put("uin",uin);
			response_json.put("vin",0L);
			response_json.put("status","Failed");
			response_json.put("message","Invalid UIN");
			response_json.put("timestamp",sdf.format(new Date(System.currentTimeMillis())));
			return response_json;
			*/
			return new VINGeneration(uin , 0L,"Failed","Invalid UIN",sdf.format(new Date(System.currentTimeMillis())));
		}else {
			Resident res = residentrepo.findById(uin).orElse(null);
			if(res != null) {
				//**********//return as json object with key val pair
				/*
				response_json.put("uin",uin);
				response_json.put("vin",System.currentTimeMillis());
				response_json.put("status","OK");
				response_json.put("message","Accepted");
				response_json.put("timestamp",sdf.format(new Date(System.currentTimeMillis())));
				*/
				VINGeneration vingen = new VINGeneration(uin , System.currentTimeMillis(),"OK","Accepted",sdf.format(new Date(System.currentTimeMillis())));
				vinrepo.save(vingen);
				return vingen;			
			}
			else {
				return new VINGeneration(uin , 0L,"Failed","No UIN Exists",sdf.format(new Date(System.currentTimeMillis())));
			}
		
		}
	}
	

	@RequestMapping(value="/vin/generate" ,method=RequestMethod.GET)
	@ResponseBody
	public VINGeneration genVINGET(@RequestBody VINGeneration vinG) {
		//String uin=vinG.getUin();
		ReadContext ctx = com.jayway.jsonpath.JsonPath.parse(vinG);
		String uin = ctx.read("$['request']['identity']['uin']");
		if(uin==null||uin.equalsIgnoreCase("NA")) {
			return new VINGeneration(uin , 0L,"Failed","Invalid UIN",sdf.format(new Date(System.currentTimeMillis())));
			
		}else {
			Resident res = residentrepo.findById(uin).orElse(null);
			if(res != null) {
				VINGeneration vingen = new VINGeneration(uin , System.currentTimeMillis(),"OK","Accepted",sdf.format(new Date(System.currentTimeMillis())));
				vinrepo.save(vingen);
				return vingen;			
			}
			else {
				return new VINGeneration(uin , 0L,"Failed","No UIN Exists",sdf.format(new Date(System.currentTimeMillis())));
			}
			
		}
		
	}
	
	
	@RequestMapping(value="/vin/revoke" ,method=RequestMethod.POST)
	@ResponseBody
	public VINRevoke revVINPOST(@RequestBody JSONObject vinG) {
		ReadContext ctx = com.jayway.jsonpath.JsonPath.parse(vinG);
		String uin = ctx.read("$['request']['identity']['uin']");
		if(uin==null||uin.equalsIgnoreCase("NA")) {
			
			return new VINRevoke("Failed","Invalid UIN",sdf.format(new Date(System.currentTimeMillis())));
		}else {
		return new VINRevoke("OK","VIN Revoked Successfully",sdf.format(new Date(System.currentTimeMillis())));
		}
	}
	

	@RequestMapping(value="/vin/revoke" ,method=RequestMethod.GET)
	public VINRevoke revVINGET(@RequestBody JSONObject vinG) {
		ReadContext ctx = com.jayway.jsonpath.JsonPath.parse(vinG);
		String uin = ctx.read("$['request']['identity']['uin']");
		//String uin=vinG.getUin();
		if(uin==null||uin.equalsIgnoreCase("NA")) {
			return new VINRevoke("Failed","Invalid vin",sdf.format(new Date(System.currentTimeMillis())));
		}else {
			VINGeneration vingen = vinrepo.findById(uin).orElse(null);
			if(vingen != null) {
				vinrepo.deleteById(uin);
			    return new VINRevoke("OK","VIN Revoked Successfully",sdf.format(new Date(System.currentTimeMillis())));
			}
			else {
			    return new VINRevoke("Failed","No VIN Generated",sdf.format(new Date(System.currentTimeMillis())));

			}
			
		}
	}
	
	
	
	@RequestMapping(value="/uin/reprint" ,method=RequestMethod.POST)
	public UINReprintResponse requestReprintPOST(@RequestParam(value="uin", defaultValue="NA") String uin) {
		//ReadContext ctx = com.jayway.jsonpath.JsonPath.parse(vinG);
		//String uin = ctx.read("$['request']['identity']['uin']");
		if(uin==null||uin.equalsIgnoreCase("NA")) {
			return new UINReprintResponse(null,"Failed","Invalid UIN",sdf.format(new Date(System.currentTimeMillis())));
		}
		else {
			Resident res = residentrepo.findById(uin).orElse(null);
			if(res != null) {	
				return new UINReprintResponse(res,"OK","Reprint Successfully",sdf.format(new Date(System.currentTimeMillis())));			
			}
			else {
				return new UINReprintResponse(res,"Failed","UIN Not Exists",sdf.format(new Date(System.currentTimeMillis())));
			}
			
		}
	}
	

	@RequestMapping(value="/uin/reprint" ,method=RequestMethod.GET)
	public UINReprintResponse requestReprintGET(@RequestParam(value="uin", defaultValue="NA") String uin) {
		if(uin==null||uin.equalsIgnoreCase("NA")) {
			return new UINReprintResponse(null,"Failed","Invalid UIN",sdf.format(new Date(System.currentTimeMillis())));
		}
		else {
			Resident res = residentrepo.findById(uin).orElse(null);
			if(res != null) {	
				return new UINReprintResponse(res,"OK","Reprint Successfully",sdf.format(new Date(System.currentTimeMillis())));			
			}
			else {
				return new UINReprintResponse(res,"Failed","UIN Not Exists",sdf.format(new Date(System.currentTimeMillis())));
			}
			
		}
	}
	
	@RequestMapping(value="/uin/update" ,method=RequestMethod.POST)
	public AddressResponse updateRequest(@RequestBody Resident resident) {
		AddressResponse resp=validateData(resident);
		if(resp.getStatus().equalsIgnoreCase("Failed"))return resp;
		else {
			residentrepo.save(resident);
			return resp;
		}
		
	}
	//
	public AddressResponse validateData(Resident resident) {
		AddressResponse resp=new AddressResponse();
		resp.setUin(resident.getUin());
		resp.setTimestamp(sdf.format(new Date(System.currentTimeMillis())));
		if(resident.getCity()==null||resident.getCity().length()<=0) {
			resp.setMessage("Invalid City");
			resp.setStatus("Failed");
		}else if(resident.getDist()==null||resident.getDist().length()<=0) {
			resp.setMessage("Invalid District");
			resp.setStatus("Failed");
		}else if(resident.getStreet()== null ||resident.getStreet().length()<=0) {
			resp.setMessage("Invalid Street");
			resp.setStatus("Failed");
		}else if(resident.getState()== null ||resident.getState().length()<=0) {
			resp.setMessage("Invalid State");
			resp.setStatus("Failed");
		}else if(resident.getHouse_no()== null ||resident.getHouse_no().length()<=0) {
			resp.setMessage("Invalid House No.");
			resp.setStatus("Failed");
		}else {
			resp.setMessage("Request Recieved");
			resp.setStatus("Success");
		}
		return resp;
	}

}
