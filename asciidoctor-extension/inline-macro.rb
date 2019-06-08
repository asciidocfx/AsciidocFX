Asciidoctor::Extensions.register do
  inline_macro do
    use_dsl
    named :blokmacro
    using_format :short
    name_positional_attributes :target, :file, :extension

    process do |parent, target, attrs|
      create_inline parent, "image", attrs
    end
  end
end