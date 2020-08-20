package com.android;

public class InfoCommTypeDef {

		public interface OnInfoListener
		{
			void OnInfoStatus(int data);
		}

		public interface OnInfoLogicListener
		{
			void OnInfoStatusDeal(int data);
			void OnSmsVerify(int data);
		}
		public interface OnInfoArbitractionListener
		{
			void OnInfoStatusDeal(int data);
		}

}

