/**
 * The BSD License
 *
 * Copyright (c) 2010, 2011 RIPE NCC
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   - Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   - Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *   - Neither the name of the RIPE NCC nor the names of its contributors may be
 *     used to endorse or promote products derived from this software without
 *     specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.ripe.rpki.validator.bgp.preview
import net.ripe.ipresource.Asn
import net.ripe.ipresource.IpRange
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import java.io.Reader
import java.io.BufferedReader
import scala.collection.JavaConverters._
import java.util.zip.GZIPInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.net.URL

object RisWhoisParser {

  val regex = """^\s*([0-9]+)\s+([0-9a-fA-F.:/]+)\s+([0-9]+)\s*$""".r
  
  def parseFromUrl(url: URL): Iterator[BgpRisEntry] = {
    val reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(url.openStream())))
    Iterator.continually(reader.readLine()).takeWhile(_ != null).flatMap(parseLine(_))
  }

  
  def parseLine(content: String): Option[BgpRisEntry] = {
    content match {
      case regex(asn, ipprefix, visibility) => Some(new BgpRisEntry(origin = Asn.parse(asn), prefix = IpRange.parse(ipprefix), visibility = Integer.parseInt(visibility)))
      case _ => None
    }
  }
  
  
}

case class BgpRisEntry(origin: Asn, prefix: IpRange, visibility: Int)
