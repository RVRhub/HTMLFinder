package com.rybak.htmlfinder

import java.io.FileNotFoundException

import org.scalatest.{FlatSpec, Matchers}

import scala.util.{Failure, Success, Try}

class HTMLFinderTest extends FlatSpec with Matchers {

  val resourcePathOrig = "samples/sample-0-origin.html"
  val targetElementId: String = "make-everything-ok-button"

  "A HTMLFinder" should "[Test1] load html's urls and match same tag" in {
    val resourcePathSecond = "samples/sample-1-evil-gemini.html"

    HTMLFinder.runMatcher(resourcePathOrig, resourcePathSecond, targetElementId) should be (Success("html > body > div > div[1] > div[2] > div > div > div[1] > a[1]"))
  }

  "A HTMLFinder" should "[Test2] load html's urls and match same tag" in {
    val resourcePathSecond = "samples/sample-2-container-and-clone.html"

    HTMLFinder.runMatcher(resourcePathOrig, resourcePathSecond, targetElementId) should be (Success("html > body > div > div[1] > div[2] > div > div > div[1] > div > a"))
  }

  "A HTMLFinder" should "[Test3] load html's urls and match same tag" in {
    val resourcePathSecond = "samples/sample-3-the-escape.html"

    HTMLFinder.runMatcher(resourcePathOrig, resourcePathSecond, targetElementId) should be (Success("html > body > div > div[1] > div[2] > div > div > div[2] > a"))
  }

  "A HTMLFinder" should "[Test4] load html's urls and match same tag" in {
    val resourcePathSecond = "samples/sample-4-the-mash.html"

    HTMLFinder.runMatcher(resourcePathOrig, resourcePathSecond, targetElementId) should be (Success("html > body > div > div[1] > div[2] > div > div > div[2] > a"))
  }

  it should "not find same tag in second html" in {
    the [Exception] thrownBy {
      val resourcePathSecond = "samples/sample-4-empty.html"
      HTMLFinder.runMatcher(resourcePathOrig, resourcePathSecond, targetElementId).get
    } should have message ("Tag is not found")
  }

  it should "not find origin tag" in {
    the [Exception] thrownBy {
      val resourcePathSecond = "samples/sample-4-empty.html"
      HTMLFinder.runMatcher(resourcePathSecond, resourcePathSecond, targetElementId).get
    } should have message ("Cannot find origin element by " + targetElementId)
  }

  it should "not find document" in {
    the [FileNotFoundException] thrownBy {
      val resourcePathSecond = "samples/sample-5.html"
      HTMLFinder.runMatcher(resourcePathSecond, resourcePathSecond, targetElementId).get
    }
  }

}
