
# react-native-upi-pay
react-native-upi is a plugin to integrate the UPI payment interface made by NPCI from your react native apps. This plugin allows you to enable peer to peer payments via UPI in your react native apps. Linking specs have been followed as per this doc

## Getting started

`$ npm install react-native-upi-pay --save`

### Mostly automatic installation

`$ react-native link react-native-upi-pay`

### Manual installation

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.upiPay.UpiPayPackage;` to the imports at the top of the file
  - Add `new UpiPayPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-upi-pay'
  	project(':react-native-upi-pay').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-upi-pay/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-upi-pay')
  	```


## Usage (Example of react-native-upi-pay )
```javascript



import React, {Component} from 'react';
import {View,Text,Button} from 'react-native'; 
import RNUpiPayment from 'react-native-upi-pay';

/*
    npm install react-native-upi-pay
    react-native link
*/

export default class App extends Component{
    constructor(props){
        super();
        this.state={
            Status:"", 
            txnId:"",
            GOOGLE_PAY:'GOOGLE_PAY',
			PHONEPE:'PHONEPE',
			PAYTM:'PAYTM',
            message:""
        }
    }
    render(){
        that=this;
        function floo(paymentApp){
            RNUpiPayment.initializePayment({
                vpa: 'something@bank',  		//your upi address like 12345464896@okhdfcbank
                payeeName: ' abc',   			// payee name 
                amount: '1',				//amount
                transactionNote:'Testing Upi',		//note of transaction
                transactionRef: 'aasf-332-aoei-fn'	//some refs to aknowledge the transaction
            },paymentApp,successCallback,failureCallback);
        }
        function failureCallback(data){
            console.log(data)
            // in case no action taken
            if (data['status']=="FAILURE"){
                that.setState({Status:"FAILURE"})
                that.setState({message:data['message']});
            }
            // in case of googlePay
            else if (data['Status']=="FAILURE"){
                that.setState({Status:"FAILURE"})
                that.setState({message:"app closed without doing payment"});;
            }
            // in case of phonepe
            else if (data['Status']=="Failed"){
                that.setState({Status:"FAILURE"});
                that.setState({message:"app closed without doing payment"});
            }
            // in case of phonepe
            else if(data['Status']=="Submitted"){
                that.setState({Status:"FAILURE"});
                that.setState({message:"transaction done but pending"});
            }
            // any other case than above mentioned
            else{
                that.setState({Status:"FAILURE"});
                that.setState({message:data[Status]});
            }
        }
        function successCallback(data){
            //
            console.log(data);
            that.setState({Status:"SUCCESS"});
            that.setState({txnId:data['txnId']});
            that.setState({message:"Succccessfull payment"});
        }
        return (
        <View style={{alignItems:"center",justifyContent:"center",flex:1}}>
        <View style={{flexDirection:'row',padding:5}}>
			<Button
			title="Google pay"
			onPress={() => {floo(this.state.GOOGLE_PAY)}}
			/>

			<Button
			title="Phone pe"
			onPress={() => {floo(this.state.PHONEPE)}}
			/>
			<Button
			title="PAYTM"
			onPress={() => {floo(this.state.PAYTM)}}
			/>
		</View>

        <Text>{this.state.Status+" "+this.state.txnId}</Text>
        <Text>{this.state.message}</Text>
        </View>
        );
    }
}


// TODO: What to do with the module?
RNReactNativeUpiPay;
```
  ##insipiration 
  ` This liabrary is inspired by react-native-upi-payments.
