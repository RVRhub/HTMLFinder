# AgileEngine backend-XML Scala snippets

It is a tool for finding similar tags in two documents with maximum compatibility of the original tag.
Search is performed within the body tag.
Target Element Id witch tool use for finding origin tag have value: "make-everything-ok-button", but the current code is very easy to adapt to work with any tag id.
 
 
Input: url to input_origin_file_path input_other_sample_file_path

Output: path to found tag in input_other_sample_file_path 

    Example: html > body > div[0] > div[1] > div[2] > div[0] > div > div[1] > div > a

###System Requirements 
Java: 1.8

###How to run 
java -jar samples/sample-0-origin.html samples/sample-2-container-and-clone.html 