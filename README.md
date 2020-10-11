# MyExpenses
This is a Android app that analyses all the SMS of the user ,then finds all the banking transactional SMS's and calculate the Income and expenses based on it and display in the Pie-chart .

# Nothing special instructions is needed to run this app

## Allow the permission on start up of the app to read the SMS from Your device.
## Tested on latest versions like  API level 26 ,27 ,28 ,29


## Table of Contents
1.First screen display the Pie-chart combining all transactions , Income and Expense.
2.First screen has a Floating button which will take you to the next screen which displayes the all the bank transactional messages.
3.Screen second has a filter button on the tool bar and swipe-down action to refresh the data.

## Design pattern
1. Used MVVM(Model View ViewModel) architecture.
2. Used Jetpack components like DataBinding , Corutines , Navigation component.
3. Used Koin for Dipendency Injection.
4. Used Material design components and few third party libraries.


## Code flow

1. On start up of the app checks the permission to read the sms if it is denied it ask for the permission.
2. As soon as the permission is granted , all logic to read and analyze the sms will happen in the repository
   class and results will be observed from the ui through SharedViewModel.
3. SharedViewModel class is shared between the two Fragment so that all reading and
   analysis of the sms happens only once.
4. Only the Pull-down to refresh action triggers the repository class to analyze the
   sms again to fetch new data if there are new sms otherwise it always uses the cached data.
5. User can filter the sms to minimizes the results.





