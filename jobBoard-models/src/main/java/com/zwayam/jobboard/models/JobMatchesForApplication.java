package com.zwayam.jobboard.models;

import java.util.ArrayList;
import java.util.List;

import com.zwayam.ai.parser.model.ParsedResult;
import com.zwayam.ai.parser.model.Profile;


public class JobMatchesForApplication {

		public JobMatchesForApplication(JobApplication jobApplication) {
			jobs = new ArrayList<Job>();
			this.jobApplication = jobApplication;
		}
		
		public JobMatchesForApplication() {
			jobs = new ArrayList<Job>();
		}

		public JobMatchesForApplication(JobApplication jobApplication, Job job) {
			jobs = new ArrayList<Job>();
			jobs.add(job);
			this.jobApplication = jobApplication;
		}

		public JobMatchesForApplication(JobApplication jobApplication, List<Job> jobs) {
			this.jobs = jobs;
			this.jobApplication = jobApplication;
		}

		public JobMatchesForApplication(JobApplication jobApplication, List<Job> jobs,Profile profile) {
			this.jobs = jobs;
			this.jobApplication = jobApplication;
			this.profile = profile;
		}

		public JobMatchesForApplication(JobApplication jobApplication, Profile profile) {
			this.jobApplication = jobApplication;
			this.profile = profile;
		}
		public JobMatchesForApplication(JobApplication jobApplication, List<Job> jobs,ParsedResult parsedResult) {
			this.jobs = jobs;
			this.jobApplication = jobApplication;
			this.parsedResult = parsedResult;
		}
		public JobApplication jobApplication;
		public List<Job> jobs;
		public Profile profile;
		public ParsedResult parsedResult;

		

}
