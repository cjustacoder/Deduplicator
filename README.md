## I. Documentation

### This program is: Deduplication
This task is about designing a deduplication system that can save similar files with more efficiency in space
utilization.

### Group members:
- Zhongyuan Cai (zycai@bu.edu)
- Yicun Hou (yicunhou@bu.edu)
- Haoxuan Jia (hxjia@bu.edu)
- Zifan Wang (chesterw@bu.edu)
- Boyang Zhou (zby@bu.edu)

### High-level description:
- Basic idea is to split the input file into fixed pieces and calculate each piece's hashcode(MD5). Each file saved in the locker has a pair of info which is like `{filename, [hashcodes,hashcodes,...]}` and this part is saved in the mataData. Also there's a simplified bloom filter(using only one hash function) that record each hashcodes and count its numbers which makes it easier for deletion. If file to be saved has some part with same hashcode as recorded in the mata, there's no need to save that piece and that's how deduplication works.
- To save the folder, we used `HashMap<folderName, <fileName, filePath>>` to track the folder and sub-file's directory logic, we use recursion to build this data structure.
- There are two threads in the duplicator, the main thread and GUI thread. The GUI thread is set as `invoke later` to guarantee the main function start first, then call the GUI thread. These two thread are using `actionList` and `actionListener` to do the communication.

### Relavent Reference:
- Douceur, John R, et al. “[Reclaiming Space from Duplicate Files in a Serverless Distributed File System](https://ieeexplore.ieee.org/document/1022312)” Microsoft Research, IEEE Computer Society, 2002.
- Fay Chang, Jeffrey Dean, et al. "[Bigtable: A Distributed Storage System for Structured Data](https://static.googleusercontent.com/media/research.google.com/zh-CN//archive/bigtable-osdi06.pdf)" OSDI, 2006.
- Geneviève, Jomier, et al. [Storage and management of similar images](http://www.scielo.br/scielo.php?script=sci_arttext&pid=S0104-65002000000100003&lng=en&nrm=iso&tlng=en) University of Paris, Journal of the Brazilian Computer Society, 2002, pp. 13–25.
- Ghemawat, Sanjay, et al. “[The Google File System](http://static.googleusercontent.com/media/research.google.com/en/us/archive/gfs-sosp2003.pdf)” Google, 2003.
- Quinlan, S. and Dorward, S."[Venti Venti: a new approach to archival storage](https://www.usenix.org/legacy/publications/library/proceedings/fast02/quinlan/quinlan.pdf)", USENIX Association, 2002.
- Xu Chu, Ihab F. Ilyas, Paraschos Koutris. "[Distributed Data Deduplication](http://www.vldb.org/pvldb/vol9/p864-chu.pdf)" Univ of Waterloo, 2016.

### Extra Features:
**1. Substring search**
- Appoint a specific encoding format ( ASCII, UTF8 or UTF16 ).
- For each sliced file in the locker, match the substring with content, and record the hashcode file where substring matches.
- Search in the mataData, a structure which is like `{filename, [hashcodes.....]}`, in this case `Hashmap<String, ArrayList<String>>` especially. Print out the filename that matches with a recoreded hashcode (recorded above).


**2. File Deletion**
- The File Deletion implemented only supports the deletion of files.
- The data structure accessed are the mataData and the hashmap which records the numbers each hashcode is used.
- For each deletion operation, first find the hashcode list corresbonding to the filename in metadata, then go through the hashcode list, decrease the count of each hashcode by 1, if any reaches 0, then delete the hashcode file in locker and remove the hashcode key in count hashmap. Finally remove the filename key in mataData. 

**3. Networked access**
- Create a ssh tester account. While use want to use network access run the project, use the tester account to login to the server, then go to the project directory and run the project as usual. The file transfer will use ssh command. This feature needs reservation before testing, since the server is deployed in personal laptop and we need to run the server to support this function.

**4. Store directories of files as one entity**
- The data structure to save the folder information is to build a `HashMap<FolderName,HashMap<FilePath, FileName>>`, this hashmap keeps to the directory logic of the original folder. 
- For saving the blank folder, we treat each folder as a file as will. 
  - While saving the folder, the hashcode of the folder is set to -1; 
  - While saving the file, the hashcode is the real hash code of the file. 
- On the contrary, while retrieving the folder or file:
  - If the program notice the hashcode is -1, the program will create a folder;
  - If the program notice the hashcode is not -1, the program will restore the file to the original path.
- The above implementation guarantees the original dirctory logic of the input folder. Also, use can retrieve the file under the folder individually once they saved the folder as one entity.
- The program will check the input is file or folder automatically, users don't need to give extra command to save or retrieve a folder.

**5. Real-time GUI**
- Show the status of storage(ot retrieving) progress in a textArea in GUI in real time. The function is implemented by `java.swing` 
- Show storage statics of locker which is using currently. Using method defined in MataData to print storage status from locker to a textArea in GUI. 
- Implement three locker operations in GUI. Using `addActionListener` to add action to bottoms which link buttom to corresponding functions. And functions can get input arguments by GUI window by using `java.swing` component and corresponding method. 

**6. Store MySQL databases**
- Store the relation of database and tables in a hashmap database_table like `{database, [table, ...]}`.
- Create a class column storing the name, type, size of table columns and store the relation of table and columns in a hashmap table_column like `{table, [column, ...]}`.
- When storing a database, store the information of the database in the hashmaps, convert tables into text files and store as a file.
- When retrieving a database, first retrieve the text files, then recreate the database, tables from the hashmaps and load text files into tables.

(partially implemented and not included in current repository)

**7. Store similar images**

Since some image formats have already encoded the image, I implemented deduplication for a relatively simple image format called BMP. The BMP file format, also known as bitmap image file or device independent bitmap (DIB) file format or simply a bitmap, is a raster graphics image file format used to store bitmap digital images. 
- Cut the input images into 16 tiny images, convert .bmp files into byte arrays and store them as block chunks. Use the file's MD5 fingerprints as its file name. If images have same tiny image chunks, those part of the file can be deduplicate.
- When retrieveing image, deduplicator will locate the target chunks according to meta data, transfer the byte file into image buffer and merger them as a whole image.

## II. Code
- Complete Java Code
```
Deduplicator (root folder of the project)
          |- out
          |    |— production
          |                |— DeDuplicator (Compiled java program)
          |                              |- test.txt (Test file for the demo)
          |								 |- test1.txt (Test file for the demo)
          |								 |- testCaseGenerater.py (Test file for the demo)
          |							     |- test1.bmp (image file for the demo)
          |                              |- test.bmp (image file for the demo)
          |- src
          |    |— CmdLine.java (Program to process the user input)
          |    |— dedup.java (Main function of the project)
          |    |— delete.java (Program to delete specific files)
          |    |— Extract.java (Program to extract save file in the locker)
          |    |— GUIFrame.java (Program to run the GUI thread)
          |    |— MainFrame.java (Design of the GUI)
          |    |— MataData.java (Program to save the metadata of folder and files, file deduplication)
          |    |— SaveFolder.java (Program save and retrieve the folder)
          |    |— SplitFile.java (Program to split hash the input file)
          |    |— Test.java (Test functions to make sure the program correctness)
          |    |— Tools.java (Accompanied tools for the whole project)
          |- test.txt (Test file for the demo)
          |- test1.txt (Test file for the demo)
          |- testCaseGenerater.py (Test file for the demo)
          |- test1.bmp (image file for the demo)
          |- test.bmp (image file for the demo)
```
- All the code that needed to run the project is under `/group7/Deduplicator/`
- Testing code is in the `Test.java`, located in `/group7/DeDuplicator/src/Test.java`

## III. Work breakdown
### Zhongyuan Cai
- Design Matadata structure and corresponding managment method.
  - manage and update storage static when operation perfomed.
  - write(read) storage static to(from) a serilized file, which make static infomation portable and nonvolatile.
- Implement GUI display and locker operation. 
  - Show saving and retrieving progress in real time.
  - Show storage static infomation of current locker.
  - Implment adding, retrieving, searching function in GUI. 
### Yicun Hou
- Command line processing and function call
- Ability to store directories of files as one entity, including:
  - Save and retieve blank foler
  - Save and retrieve folder and subfolder's content
  - Ability to retrieve folder's files individually
  - Ability to retrieve folder as original directory logic
- Develop networked access to the locker

### Haoxuan Jia
- Implement retrieve function
- Implement file deletion from the locker
- Ability to store similar mysql databases

### Zifan Wang
- Implement tools: MD5 encodeing, check path, make directory, search string in file
- Basic structure of the main function (main function from the prototype version)
- Implement Substring search
- All test cases (till prototype version)

### Boyang Zhou
- File split: Transfer the file into byte array, and divide the file into chunks, each of the file chunk has a fixed size
- Image deduplication: Since some of the image storage format has already been compressed, I basically realized the BMP image deduplication. By cutting the image into n * n small images, Those same image chunks can be deduplicate
- Merge Image: All the BMP images are cut into parts and transformed into byte array, so the merge method transformed byte arrays back into images, and merge n * n images as a whole image
- Test cases for image operation

## IV. Jira and Code Review
- The working pregress can be tracked in [Jira](https://agile.bu.edu/jira/projects/GROUP7/summary).
- If you want to do code review, please go to the [Crucible](https://agile.bu.edu/crucible/project/GROUP7?max=30&projectKey=GROUP7&view=fe).


