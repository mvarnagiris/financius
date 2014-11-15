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
