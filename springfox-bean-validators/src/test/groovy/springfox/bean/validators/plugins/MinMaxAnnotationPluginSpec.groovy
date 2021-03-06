/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package springfox.bean.validators.plugins

import com.fasterxml.classmate.TypeResolver
import spock.lang.Specification
import spock.lang.Unroll
import springfox.bean.validators.plugins.models.MinMaxTestModel
import springfox.documentation.builders.ModelPropertyBuilder
import springfox.documentation.service.AllowableRangeValues
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.contexts.ModelPropertyContext

class MinMaxAnnotationPluginSpec extends Specification {
  def "Always supported" () {
    expect:
      new MinMaxAnnotationPlugin().supports(types)
    where:
      types << [DocumentationType.SPRING_WEB, DocumentationType.SWAGGER_2, DocumentationType.SWAGGER_12]
  }

  @Unroll
  def "@Min/@Max annotations are reflected in the model #propertyName that are AnnotatedElements"()  {
    given:
      def sut = new MinMaxAnnotationPlugin()
      def element = MinMaxTestModel.getDeclaredField(propertyName)
      def context = new ModelPropertyContext(
          new ModelPropertyBuilder(),
          element,
          new TypeResolver(),
          DocumentationType.SWAGGER_12)
    when:
      sut.apply(context)
      def property = context.builder.build()
    then:
      def range = property.allowableValues as AllowableRangeValues
      range?.max == expectedMax
      range?.min == expectedMin
    where:
      propertyName      | expectedMin                   | expectedMax
      "noAnnotation"    | null                          | null
      "onlyMin"         | "10.0"                        | Double.MAX_VALUE.toString()
      "onlyMax"         | (-Double.MAX_VALUE).toString()| "20.0"
      "both"            | "10.0"                        | "20.0"
  }
}
