###Version: 0.18.1
- ```fix``` Fixed currency exchange rates update.
- ```fix``` Categories report shows tag amounts again.

###Version: 0.18.0
- ```new``` New trends graph that is interactive.
- ```new``` Updated settings page UI.
- ```new``` Updated tags list page UI.
- ```new``` Updated tag detail page UI.
- ```new``` Showing trends graph in tag detail page.
- ```new``` Small UI improvements in tag edit page.
- ```new``` Updated category list page UI.
- ```new``` Updated category detail page UI.
- ```new``` Showing trends graph in category detail page.
- ```new``` Updated category edit page UI.
- ```new``` When selecting category color you can now choose from predefined list or pick a color manually.
- ```new``` Updated currencies list page UI.
- ```new``` Updated currency detail page UI.
- ```new``` Updated currency edit page UI.
- ```new``` Currencies list has pull to refresh.
- ```new``` Updated accounts list page UI.
- ```new``` Supporting exchange rates from all currencies to all currencies.
- ```new``` Manual setting of currencies exchange rate is gone for now.
- ```new``` Updated accounts list page UI.
- ```new``` Updated account detail page UI.
- ```new``` Updated account edit page UI.
- ```new``` Account detail page has balance graph for selected period.
- ```new``` Add new transaction button is now moved to the bottom.
- ```new``` Navigation drawer UI.
- ```new``` Updated transactions list page UI.
- ```new``` Updated transactions detail page UI.
- ```new``` Better typography throughout the app. Especially on pre-Lollipop.
- ```new``` Added a bunch of new languages. Updated all other translations.
- ```fix``` Unnoticeable UI improvements to pie chart.
- ```fix``` Updated separator UI, no longer too white on older versions of Android.
- ```fix``` When selecting account, navigation drawer is no longer visible.

###Version: 0.17.1
- ```fix``` Fixed a crash on older versions of Android when starting the app.

###Version: 0.17.0
- ```new``` Updated navigation drawer UI.
- ```new``` Moved settings to navigation drawer.
- ```new``` Showing required fields when trying to make transaction confirmed.
- ```new``` Auto complete for new transaction is back. This time should be less confusing. Fields will be auto completed only once.
- ```new``` Added link to translations in about section.
- ```fix``` In calculator, when there is an operator at the end after pressing = that operator will be ignored.

###Version: 0.16.1
- ```new``` Note no longer works as a template for now. It will come back later.
- ```new``` Fields are not longer populated automatically, due to being very confusing. Now a drop down will show with most likely items for you to pick.

###Version: 0.16.0
- ```fix``` Transfers between accounts with different currencies show exchange rate and other currency fields.

###Version: 0.15.6
- ```fix``` Transaction edit working again.
- ```fix``` Alphabetical tag ordering works better with upper and lowercase letters.
- ```fix``` Note auto-complete will not show suggestions from deleted transactions and from transactions of different type.

###Version: 0.15.5
- ```new``` Note field in new transaction screen now has auto complete.
- ```new``` Note field works as template.
- ```new``` New transaction screen auto completes all other fields based on entered data.
- ```new``` Updated icons.
- ```new``` Auto-completed fields have transparent icon.
- ```new``` Clicking on auto-completed field icon confirms that field.
- ```new``` Updated translations.

###Version: 0.15.4
- ```new``` Updated UI for new transaction screen. More improvements are still to come.

###Version: 0.15.3
- ```new``` Updated translations.

###Version: 0.15.2
- ```fix``` Actually fixed Android 4.2.2 issue where app is crashing because some manufacturers are dumb.

###Version: 0.15.1
- ```fix``` Attempt to fix Android 4.2.2 issue where app is crashing because some manufacturers are dumb.

###Version: 0.15.0
- ```new``` Added PIN lock. To enable, go to Settings-Security.
- ```new``` Traditional Chinese translations are also applied to Honk Kong.
- ```fix``` Fixed exchange rate precision.

###Version: 0.14.9
- ```fix``` App launch crash fixed.

###Version: 0.14.8
- ```new``` Added new translations: Tamil, Ukrainian, Turkish.
- ```new``` Updated translations: Chinese Simplified, Slovak, Portuguese Brazil.
- ```fix``` Backup import fixes.
- ```fix``` General stability improvements.

###Version: 0.14.7
- ```fix``` Backup export fixes.

###Version: 0.14.6
- ```new``` Added Bulgarian translations.
- ```new``` Updated translations: Chinese Simplified, Slovak.
- ```fix``` Possible fix for devices that could not restore backup from file.
- ```fix``` Squashed some other bugs.

###Version: 0.14.5
- ```new``` Titles in transactions list headers now include more detail if necessary.
- ```new``` Updated translations.
- ```fix``` Squashed some bugs.

###Version: 0.14.4
- ```new``` New translations: Polish, Portuguese, Slovak.
- ```new``` Updated translations: Chinese Simplified, Chinese Traditional.
- ```fix``` Squashed some bugs.
- ```fix``` Import/Export fixes.
- ```fix``` Fixed import tags issue.
- ```fix``` Transfers can have tags again.

###Version: 0.14.3
- ```new``` Added Chinese Traditional translations.
- ```new``` Updated German, Hungarian, Russian, Brazilian translations.
- ```fix``` Fixed backup import bug.
- ```fix``` Potentially solved export backup crash.
- ```fix``` Various other bugs squashed.

###Version: 0.14.2
- ```new``` Added new languages: Chinese Simplified, Czech, French, German, Hungarian, Italian, Lithuanian, Portuguese Brazilian, Russian, Slovenian, Spanish.
- ```fix``` Fixed some crashes in transaction and transaction edit views.

###Version: 0.14.1
- ```new``` Added trends graph for expenses in overview screen.
- ```new``` Added Crashlytics for better crash tracking. I will be able to fix things better.
- ```new``` Better Google Analytics tracking.
- ```fix``` CSV export adds decimals to amount.

###Version: 0.14.0
- ```new``` Improved UI for categories report
- ```fix``` When editing category you can no longer change category type to prevent data getting corrupt.
- ```fix``` Should fix data in database to avoid any weird calculations. Also ensures that all data has proper IDs. This means that old backups will not be supported.

###Version: 0.13.0
- ```new``` Landscape layout for categories report.
- ```new``` Categories report breaks down each category into tags.
- ```fix``` Tags are now wrapping in transactions list to avoid strange symbols when they do not fit.
- ```fix``` Can create income categories again.
- ```fix``` Fixed calculation differences between categories amount and transactions list.

###Version: 0.12.1
- ```fix``` Fixed crash when refreshing currencies or trying to enter or edit a transaction.

###Version: 0.12.0
- ```new``` Landscape layout for calculator. (**@i906**)
- ```new``` Some UI improvements overall.
- ```new``` Currency detail screen landscape layout.
- ```new``` Tag detail and edit screen landscape layout.
- ```new``` Categories detail and edit screen with landscape layout.
- ```new``` Categories list now shows bot expense and income categories in one page.
- ```new``` Improvements for accounts detail and edit page in portrait and landscape mode.
- ```new``` New UI for transaction details screen. It's still a work in progress and it will change.
- ```fix``` Account balance calculation.
- ```fix``` Can edit main currency.
- ```fix``` Better overview landscape support.
- ```fix``` CSV export now uses commas as separators so now it's easier to import it to Google Spreadsheets. (Thanks to **Sunny Jerry**)
- ```fix``` Rotating screen in calculator will not lose the data anymore. (**@i906**)

###Version: 0.11.6
- ```new``` Tags are ordered alphabetically.
- ```new``` When transferring money between accounts with different currencies now you can set either exchange rate or receiving amount.
- ```new``` When in transaction edit screen, long pressing on a field will clear it.
- ```new``` Overview landscape layout.
- ```fix``` Fixed calculator crash.
- ```fix``` Fixed import when categories went missing. (**@i906**)
- ```fix``` Fixed calculator buttons on pre-Lollipop devices. (**@i906**)
- ```fix``` Better handling of large numbers in calculator.
- ```fix``` Fixed disappearing note issue.
- ```fix``` Title not resetting any more when rotating device in home screen.

###Version: 0.11.5
- ```fix``` Fixed account balance calculation. Again.

###Version: 0.11.4
- ```new``` Small UI improvements on home screen.
- ```fix``` Fixed account balance calculation.
- ```fix``` In categories report instead of 0% when rounded down now there is <1%

###Version: 0.11.3
- ```fix``` Fixed crash when rotating phone in main screen.

###Version: 0.11.2
- ```fix``` Fixed backup import.

###Version: 0.11.1
- ```new``` Overall UI improvements.
- ```fix``` Crash when deleting account.
- ```fix``` Crash when importing from Google Drive. Old backups still won't work.
- ```fix``` Crash when exporting to file in languages that contain special characters.
- ```fix``` Crash in category report when there are transactions without a category.
- ```fix``` Calculator return 0 when decimals where used in certain languages.
- ```fix``` Various other crashes.

###Version: 0.11.0
- ```fix``` Up button is working.
- ```new``` Supporting Jelly Bean and up.

###Version: 0.10.0
- ```new``` Completely new UI. Material design.
- ```new``` Tags for transactions.
- ```new``` Import backup file from device or Google Drive.
- ```new``` Export backup file to device or Google Drive.
- ```new``` Export CSV to phone to device or Google Drive.
- ```new``` Improved CSV format.
- ```new``` No more sub-categories. Use tags instead.
