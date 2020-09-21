package com.zwayam.jobboard.models;

public class Status extends BaseObjectModel{

	private int code;
	private String message;
	public String additionalInfo;
	private String additionalInfo1;
	private Character additionalInfo2;
	private String additionalInfo3;
	private String additionalInfo4;
	private Object result;
	public Integer jobApplyId;
	public String jobId;
	public Integer companyId;
	public String jobSpecificationUrl;
	public String fileName;
	public int data;
	public int size;
	private int  index;
	public boolean hasValue;
	private Object parallelWorkFlow;

	public Status() {
		code = 200;
	}

	public Status(int code,boolean hasValue, String message) {
		this.code = code;
		this.hasValue = hasValue;
		this.message = message;
		
	}
	
	public Status(int code, String message) {
		this.code = code;
		this.message = message;
	}
	
	
	
	public Status(int code, int data,int size) {
		this.code = code;
		this.data = data;
		this.size=size;
	}
	
	public Status(int code, String message, String additionalInfo){
		this.code = code;
		this.message = message;
		this.additionalInfo = additionalInfo;
	}
	
	public Status(int code, String message, String additionalInfo, String additionalInfo1,int index){
		this.code = code;
		this.message = message;
		this.additionalInfo = additionalInfo;
		this.additionalInfo1 = additionalInfo1;
		this.index=index;
	}
	
	
	public Status(int code, String message, String additionalInfo, String additionalInfo1){
		this.code = code;
		this.message = message;
		this.additionalInfo = additionalInfo;
		this.additionalInfo1 = additionalInfo1;
			}
	public Status(int code, String message, String additionalInfo, String additionalInfo1,String additionalInfo3){
		this.code = code;
		this.message = message;
		this.additionalInfo = additionalInfo;
		this.additionalInfo1 = additionalInfo1;
		this.additionalInfo3 = additionalInfo3;
	}
	
	public Status(int code, String message, String additionalInfo, String additionalInfo1, String additionalInfo3, String additionalInfo4){
		this.code = code;
		this.message = message;
		this.additionalInfo = additionalInfo;
		this.additionalInfo1 = additionalInfo1;
		this.additionalInfo3 = additionalInfo3;
		this.additionalInfo4 = additionalInfo4;
	}
	
	public Status(int code, String message, Character additionalInfo2){
		this.code = code;
		this.message = message;
		this.additionalInfo2 = additionalInfo2;
	}

	public Status(int code,Object result){
		this.code = code;
		this.result = result;
	}
	
	public static Status getStatusForParallelWorkFlowDef(int code, Object parallelWorkFlow, String message) {
		Status status = new Status();
		status.code=code;
		status.parallelWorkFlow=parallelWorkFlow;
		status.message=message;
		return status;
	}

	public int getCode() {
		return code;
	}

	public void setData(int data) {
		this.data = data;
	}
	public int getData() {
		return data;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	public int getSize() {
		return size;
	}
	
	public void setparallelWorkFlow(Object parallelWorkFlow) {
		this.parallelWorkFlow = parallelWorkFlow;
	}
	public Object getparallelWorkFlow() {
		return parallelWorkFlow;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getAdditionalInfo1() {
		return additionalInfo1;
	}

	public void setAdditionalInfo1(String additionalInfo1) {
		this.additionalInfo1 = additionalInfo1;
	}

	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public Character getAdditionalInfo2() {
		return additionalInfo2;
	}

	public void setAdditionalInfo2(Character additionalInfo2) {
		this.additionalInfo2 = additionalInfo2;
	}
	
	public String getAdditionalInfo3() {
		return additionalInfo3;
	}

	public void setAdditionalInfo3(String additionalInfo3) {
		this.additionalInfo3 = additionalInfo3;
	}

	public String getAdditionalInfo4() {
		return additionalInfo4;
	}

	public void setAdditionalInfo4(String additionalInfo4) {
		this.additionalInfo4 = additionalInfo4;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public static Status getApplySavedSuccessResult(String saveResult, Integer applyId, String jobId, Integer companyId,
			String jobSpecificationUrl) {
		Status status = new Status(200, saveResult,"");
		status.jobApplyId = applyId;
		status.jobId = jobId;
		status.companyId = companyId;
		status.jobSpecificationUrl = jobSpecificationUrl;
		
		return status;
	}
}
