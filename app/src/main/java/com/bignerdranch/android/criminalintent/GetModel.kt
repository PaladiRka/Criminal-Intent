package com.bignerdranch.android.criminalintent;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(
    "foo",
    "bar",
    "baz"
)
public class GetModel {

    @JsonProperty("foo")
    private lateinit var foo: String

    @JsonProperty("bar")
    private lateinit var bar: Integer

    @JsonProperty("baz")
    private var baz: Boolean = false

    @JsonIgnore
    private var additionalProperties: HashMap<String, Object> = HashMap<String, Object>()

    @JsonProperty("foo")
    fun getFoo(): String {
        return foo
    }

    @JsonProperty("foo")
    fun setFoo(foo: String) {
        this.foo = foo
    }

    @JsonProperty("bar")
    fun getBar(): Integer {
        return bar;
    }

    @JsonProperty("bar")
    fun setBar(bar: Integer) {
        this.bar = bar;
    }

    @JsonProperty("baz")
    fun getBaz(): Boolean {
        return baz;
    }

    @JsonProperty("baz")
    fun setBaz(baz: Boolean) {
        this.baz = baz;
    }

    @JsonAnyGetter
    fun getAdditionalProperties(): HashMap<String, Object> {
        return this.additionalProperties;
    }

    @JsonAnySetter
    fun setAdditionalProperty(name: String, value: Object) {
        this.additionalProperties.put(name, value);
    }

}