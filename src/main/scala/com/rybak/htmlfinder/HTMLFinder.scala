package com.rybak.htmlfinder

import java.io.File
import java.util

import com.typesafe.scalalogging.LazyLogging
import org.jsoup.Jsoup
import org.jsoup.nodes.{Attribute, Attributes, Document, Element}

import scala.util.{Failure, Success, Try}

object HTMLFinder extends App with LazyLogging {

  private val CHARSET_NAME = "utf8"
  val targetElementId: String = "make-everything-ok-button"
  final val HTML: String = "html"

  runMatcher(args(0), args(1), targetElementId) match {
    case Success(result) => logger.info(result)
    case Failure(exception) => logger.error(exception.getMessage)
  }

  def runMatcher(resourcePathOrig: String, resourcePathSecond: String, targetElementId: String): Try[String] = Try{

    val originEl = findElementById(new File(resourcePathOrig), targetElementId)

    val originFeatures = originEl.get match {
      case null => throw new Exception("Cannot find origin element by " + targetElementId)
      case _ => analyzeElement(originEl.get)
    }

    val compDoc = getDocument(resourcePathSecond)
    val matchedElement = findMatchedElement(compDoc.get, originFeatures)

    matchedElement match {
      case null => throw new Exception ("Tag is not found")
      case _ => matchedElement.path
    }
  }

  def findMatchedElement(doc: Document, nodeFeatures: NodeFeatures): MatchedElement = {
    val startPath = "html";

    matchByFeatures(doc.body(), nodeFeatures, startPath, 0, 0)
  }

  def matchByFeatures(el: Element, originNodeFeatures: NodeFeatures, currPath: String, currMaxCountMatches: Int, childNum: Int): MatchedElement = {
    var matchedElement: MatchedElement = null

    val path = updatePath(currPath, el.tagName, childNum);

    var maxMatches = currMaxCountMatches
    val matches = compareAndCountMatches(el, originNodeFeatures)

    if (matches > maxMatches) {
      maxMatches = matches

      matchedElement = new MatchedElement(el, maxMatches, path)
    }

    val children = el.children()

    for (i <- 0 until children.size()) {
      val matchedChildElement: MatchedElement = matchByFeatures(children.get(i), originNodeFeatures, path, matches, i)

      if (matchedChildElement != null && matchedChildElement.countMatches > maxMatches) {
        matchedElement = matchedChildElement
        maxMatches = matchedChildElement.countMatches
      }
    }

    matchedElement
  }

  def compareAndCountMatches(el: Element, originNodeFeatures: NodeFeatures): Int = {
    val nodeFeatures = analyzeElement(el)

    countMatches(originNodeFeatures, nodeFeatures)
  }

  def findElementById(htmlFile: File, targetElementId: String): Try[Element] = Try {
    Jsoup.parse(htmlFile, CHARSET_NAME, htmlFile.getAbsolutePath)
  }.map(_.getElementById(targetElementId))

  def getDocument(htmlFilePath: String): Try[Document] = Try{
    val file = new File(htmlFilePath)
    Jsoup.parse(file, CHARSET_NAME, file.getAbsolutePath)
  }

  def analyzeElement(element: Element): NodeFeatures = {
    var nodeFeatures = new NodeFeatures(
      element.tagName(),
      element.attributes(),
      element.text
    )

    nodeFeatures
  }

  def countMatches(first: NodeFeatures, second: NodeFeatures): Int = {
    if (first.tagName != second.tagName) return 0

    def compareAttributes(attrs: util.Iterator[Attribute], currentAttr: Boolean, count: Int): Int = currentAttr match {
      case false => count
      case true => {
        val attr = attrs.next()
        val attrFromMap: String = second.tagAttributes.get(attr.getKey)
        val isMatch = if (attrFromMap != null && attrFromMap == attr.getValue) count+1 else count
        compareAttributes(attrs, attrs.hasNext, isMatch)
      }
    }

    def isSameTagValue = if(first.tagValue == second.tagValue) 1 else 0

    val iterator = first.tagAttributes.iterator()
    compareAttributes(iterator, iterator.hasNext, isSameTagValue)
  }

  def updatePath(currPath: String, tagName: String, childNum: Int): String = childNum match {
    case 0 => currPath + " > " + tagName;
    case _ => currPath + " > " + tagName + "[" + childNum + "]";
  }

  class NodeFeatures(
                      //                      val maxCountOfMatches: Int,
                      val tagName: String,
                      val tagAttributes: Attributes,
                      val tagValue: String
                    ) {
    override def toString: String = {
      "tagName: " + tagName + "; tagAttributes: " + tagAttributes + "; tagValue: " + tagValue
    }
  }

  class MatchedElement(
                        val element: Element,
                        val countMatches: Int,
                        var path: String
                      ) {
    override def toString: String = {
      "path: " + path + "; element: " + element
    }
  }

}
