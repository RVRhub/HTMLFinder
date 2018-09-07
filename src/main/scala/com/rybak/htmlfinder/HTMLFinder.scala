package com.rybak.htmlfinder

import java.io.File

import com.rybak.htmlfinder.HTMLFinder.targetElementId
import com.typesafe.scalalogging.LazyLogging
import org.jsoup.Jsoup
import org.jsoup.nodes.{Attributes, Document, Element}

object HTMLFinder extends App with LazyLogging {

  private val CHARSET_NAME = "utf8"

  /*val resourcePathOrig = "samples/startbootstrap-sb-admin-2-examples/sample-0-origin.html"
  val resourcePathSecond = "samples/startbootstrap-sb-admin-2-examples/sample-1-evil-gemini.html"*/

  // val resourcePathSecond = "samples/sample-4-empty.html"
  val targetElementId: String = "make-everything-ok-button"

  run()

  def run(): Unit = {
    val resourcePathOrig = args(0)
    val resourcePathSecond = args(1)

    val originDoc = getDocument(resourcePathOrig)
    val originEl = findElementById(originDoc, targetElementId)

    if (originEl == null) {
      println("Cannot find origin element by " + targetElementId)
      return
    }

    val originFeatures = analyzeElement(originEl)

    val compDoc = getDocument(resourcePathSecond)

    val matchedElement = findMatchedElement(compDoc, originFeatures)

    matchedElement match {
      case null => println("Tag is not found")
      case _ => println(matchedElement.path)
    }
  }

  def findMatchedElement(doc: Document, nodeFeatures: NodeFeatures): MatchedElement = {
    val startedPath = "html"

    matchByFeatures(doc.body(), nodeFeatures, startedPath, 0, -1)
  }

  def matchByFeatures(el: Element, originNodeFeatures: NodeFeatures, currPath: String, currMaxCountMatches: Int, childNum: Int): MatchedElement = {
    var matchedElement: MatchedElement = null

    var path = currPath + " > " + el.tagName()

    if (childNum != -1) {
      path += "[" + childNum + "]"
    }

    var maxMatches = currMaxCountMatches
    val matches = analyzeAndCountMatches(el, originNodeFeatures)

    if (matches > maxMatches) {
      maxMatches = matches

      matchedElement = new MatchedElement(el, maxMatches, path)
    }

    val children = el.children()

    for (i <- 0 until children.size()) {
      var numChild = -1

      if (children.size() > 1) {
        numChild = i
      }

      var matchedChildElement: MatchedElement = matchByFeatures(children.get(i), originNodeFeatures, path, matches, numChild)

      if (matchedChildElement != null && matchedChildElement.countMatches > maxMatches) {
        matchedElement = matchedChildElement
        maxMatches = matchedChildElement.countMatches
      }
    }

    matchedElement
  }

  def analyzeAndCountMatches(el: Element, originNodeFeatures: NodeFeatures): Int = {
    val nodeFeatures = analyzeElement(el)

    countMatches(originNodeFeatures, nodeFeatures)
  }


  def findElementById(doc: Document, targetElementId: String): Element = {
    doc.selectFirst("#" + targetElementId)
  }

  def getDocument(htmlFilePath: String): Document = {
    val file = new File(htmlFilePath)

    Jsoup.parse(file, CHARSET_NAME, file.getAbsolutePath)
  }

  def analyzeElement(element: Element): NodeFeatures = {
    val nodeFeatures = new NodeFeatures(
      element.tagName(),
      element.attributes(),
      element.text
    )

    nodeFeatures
  }

  def countMatches(first: NodeFeatures, second: NodeFeatures): Int = {
    if (first.tagName != second.tagName) {
      return 0
    }

    var count = 0

    if (first.tagValue == second.tagValue) {
      count += 1
    }

    first.tagAttributes.forEach(attr => {
      val attrFromMap: String = second.tagAttributes.get(attr.getKey)

      if (attrFromMap != null && attrFromMap == attr.getValue) {
        count += 1
      }
    })

    count
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
